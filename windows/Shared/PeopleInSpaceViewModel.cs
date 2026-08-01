using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows.Input;

namespace PeopleInSpace.Windows.Shared;

public sealed class PersonViewModel(PersonInfo person)
{
    public string Name { get; } = person.Name;
    public string Craft { get; } = person.Craft;
    public string? Nationality { get; } = person.Nationality;
    public string? ImageUrl { get; } = person.ImageUrl;
    public string? Biography { get; } = person.Biography;
}

public sealed class PeopleInSpaceViewModel : INotifyPropertyChanged, IAsyncDisposable
{
    private readonly IPeopleInSpaceSource _source;
    private readonly IUiDispatcher _dispatcher;
    private readonly object _lifetimeLock = new();
    private readonly CancellationTokenSource _disposeCancellation = new();
    private CancellationTokenSource? _lifetime;
    private Task? _peopleTask;
    private Task? _issTask;
    private Task? _refreshTask;
    private string _issLocation = "Waiting for ISS position…";
    private DateTimeOffset? _issTimestamp;
    private string? _peopleError;
    private string? _issError;
    private bool _peopleLoading;
    private bool _issLoading;
    private bool _hasIssPosition;
    private bool _isRefreshing;
    private PersonViewModel? _selectedPerson;
    private bool _disposed;
    private int _refreshInProgress;
    private readonly AsyncCommand _refreshCommand;

    public PeopleInSpaceViewModel(IPeopleInSpaceSource source, IUiDispatcher dispatcher)
    {
        _source = source;
        _dispatcher = dispatcher;
        _refreshCommand = new AsyncCommand(
            RefreshAsync,
            () => !IsRefreshing && Volatile.Read(ref _refreshInProgress) == 0 && !Volatile.Read(ref _disposed));
        RefreshCommand = _refreshCommand;
    }

    public ObservableCollection<PersonViewModel> People { get; } = [];

    public ICommand RefreshCommand { get; }

    public string IssLocation { get => _issLocation; private set => SetField(ref _issLocation, value); }

    public DateTimeOffset? IssTimestamp { get => _issTimestamp; private set => SetField(ref _issTimestamp, value); }

    public bool PeopleLoading { get => _peopleLoading; private set => SetField(ref _peopleLoading, value); }

    public bool IssLoading { get => _issLoading; private set => SetField(ref _issLoading, value); }

    public bool IsRefreshing
    {
        get => _isRefreshing;
        private set
        {
            if (SetField(ref _isRefreshing, value)) _refreshCommand.RaiseCanExecuteChanged();
        }
    }

    public bool HasIssPosition { get => _hasIssPosition; private set => SetField(ref _hasIssPosition, value); }

    public string? PeopleError
    {
        get => _peopleError;
        private set
        {
            if (SetField(ref _peopleError, value)) OnPropertyChanged(nameof(HasPeopleError));
        }
    }

    public bool HasPeopleError => !string.IsNullOrWhiteSpace(PeopleError);

    public string? IssError
    {
        get => _issError;
        private set
        {
            if (SetField(ref _issError, value)) OnPropertyChanged(nameof(HasIssError));
        }
    }

    public bool HasIssError => !string.IsNullOrWhiteSpace(IssError);

    public PersonViewModel? SelectedPerson { get => _selectedPerson; set => SetField(ref _selectedPerson, value); }

    public event PropertyChangedEventHandler? PropertyChanged;

    public Task StartAsync(CancellationToken cancellationToken = default)
    {
        lock (_lifetimeLock)
        {
            ObjectDisposedException.ThrowIf(_disposed, this);
            if (_lifetime is not null) return Task.CompletedTask;

            _lifetime = CancellationTokenSource.CreateLinkedTokenSource(cancellationToken, _disposeCancellation.Token);
            _peopleTask = ConsumePeopleAsync(_lifetime.Token);
            _issTask = ConsumeIssAsync(_lifetime.Token);
        }
        return Task.CompletedTask;
    }

    public Task RefreshAsync(CancellationToken cancellationToken = default)
    {
        if (Volatile.Read(ref _disposed) || Interlocked.CompareExchange(ref _refreshInProgress, 1, 0) != 0)
            return Task.CompletedTask;

        lock (_lifetimeLock)
        {
            if (_disposed)
            {
                Interlocked.Exchange(ref _refreshInProgress, 0);
                return Task.CompletedTask;
            }

            // Track the task before DisposeAsync can cancel and await it.
            _refreshTask = RefreshCoreAsync(cancellationToken);
            return _refreshTask;
        }
    }

    private async Task RefreshCoreAsync(CancellationToken cancellationToken)
    {
        using var refreshCancellation = CancellationTokenSource.CreateLinkedTokenSource(
            cancellationToken,
            _disposeCancellation.Token);
        var refreshToken = refreshCancellation.Token;

        try
        {
            await _dispatcher.InvokeAsync(() =>
            {
                PeopleError = null;
                _refreshCommand.RaiseCanExecuteChanged();
            }, refreshToken).ConfigureAwait(false);
            await _source.RefreshAsync(refreshToken).ConfigureAwait(false);
        }
        catch (OperationCanceledException) when (refreshToken.IsCancellationRequested) { }
        catch (Exception exception)
        {
            await _dispatcher.InvokeAsync(() =>
            {
                PeopleError = exception.Message;
            }, refreshToken).ConfigureAwait(false);
        }
        finally
        {
            Interlocked.Exchange(ref _refreshInProgress, 0);
            if (!Volatile.Read(ref _disposed))
                await _dispatcher.InvokeAsync(_refreshCommand.RaiseCanExecuteChanged).ConfigureAwait(false);
        }
    }

    private async Task ConsumePeopleAsync(CancellationToken cancellationToken)
    {
        try
        {
            await foreach (var snapshot in _source.WatchPeopleAsync(cancellationToken).WithCancellation(cancellationToken).ConfigureAwait(false))
                await _dispatcher.InvokeAsync(() => ApplyPeopleSnapshot(snapshot), cancellationToken).ConfigureAwait(false);
        }
        catch (OperationCanceledException) when (cancellationToken.IsCancellationRequested) { }
        catch (Exception exception)
        {
            await _dispatcher.InvokeAsync(() =>
            {
                PeopleLoading = false;
                PeopleError = exception.Message;
            }, cancellationToken).ConfigureAwait(false);
        }
    }

    private async Task ConsumeIssAsync(CancellationToken cancellationToken)
    {
        try
        {
            await foreach (var snapshot in _source.WatchIssAsync(cancellationToken).WithCancellation(cancellationToken).ConfigureAwait(false))
            {
                await _dispatcher.InvokeAsync(() =>
                {
                    IssLoading = snapshot.IsLoading;
                    IssError = snapshot.Error;
                    HasIssPosition = snapshot.HasPosition;
                    if (snapshot.HasPosition)
                    {
                        IssLocation = FormattableString.Invariant($"{snapshot.Latitude:F3}°, {snapshot.Longitude:F3}°");
                        IssTimestamp = snapshot.Timestamp;
                    }
                }, cancellationToken).ConfigureAwait(false);
            }
        }
        catch (OperationCanceledException) when (cancellationToken.IsCancellationRequested) { }
        catch (Exception exception)
        {
            await _dispatcher.InvokeAsync(() =>
            {
                IssLoading = false;
                IssError = exception.Message;
            }, cancellationToken).ConfigureAwait(false);
        }
    }

    public async ValueTask DisposeAsync()
    {
        Task[] tasks;
        CancellationTokenSource? lifetime;
        lock (_lifetimeLock)
        {
            if (_disposed) return;
            _disposed = true;
            _disposeCancellation.Cancel();
            lifetime = _lifetime;
            lifetime?.Cancel();
            tasks = new[] { _peopleTask, _issTask, _refreshTask }
                .Where(task => task is not null)
                .Cast<Task>()
                .ToArray();
        }

        try { await Task.WhenAll(tasks).ConfigureAwait(false); }
        catch (OperationCanceledException) { }
        lifetime?.Dispose();
        _disposeCancellation.Dispose();
        await _source.DisposeAsync().ConfigureAwait(false);
    }

    private void ApplyPeopleSnapshot(PeopleSnapshot snapshot)
    {
        var selectedKey = SelectedPerson is { } selected ? (selected.Name, selected.Craft) : ((string, string)?)null;
        People.Clear();
        foreach (var person in snapshot.People.OrderBy(person => person.Name, StringComparer.OrdinalIgnoreCase))
            People.Add(new PersonViewModel(person));

        PeopleLoading = snapshot.IsInitialLoading;
        IsRefreshing = snapshot.IsRefreshing;
        PeopleError = snapshot.Error;
        SelectedPerson = selectedKey is { } key
            ? People.FirstOrDefault(person => person.Name == key.Item1 && person.Craft == key.Item2)
            : null;
    }

    private bool SetField<T>(ref T field, T value, [CallerMemberName] string? name = null)
    {
        if (EqualityComparer<T>.Default.Equals(field, value)) return false;
        field = value;
        OnPropertyChanged(name);
        return true;
    }

    private void OnPropertyChanged([CallerMemberName] string? name = null) =>
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(name));

    private sealed class AsyncCommand(Func<CancellationToken, Task> execute, Func<bool> canExecute) : ICommand
    {
        public event EventHandler? CanExecuteChanged;

        public bool CanExecute(object? parameter) => canExecute();

        public async void Execute(object? parameter)
        {
            await execute(CancellationToken.None).ConfigureAwait(false);
        }

        public void RaiseCanExecuteChanged() => CanExecuteChanged?.Invoke(this, EventArgs.Empty);
    }
}
