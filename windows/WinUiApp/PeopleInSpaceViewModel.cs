using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows.Input;
using Microsoft.UI.Dispatching;
using PeopleInSpace.Kotlin;
using PeopleInSpace.Kotlin.Dev.Johnoreilly.Common.Remote;
using PeopleInSpace.Kotlin.Dev.Johnoreilly.Common.Viewmodel;

namespace PeopleInSpace.Windows.WinUiApp;

public sealed record PersonViewModel(string Name, string Craft, string? Nationality, string? ImageUrl, string? Biography);

/// <summary>
/// Owns the generated Kotlin client, collects its two StateFlows, and projects them onto bindable
/// properties on the UI thread. Generated objects are copied and disposed before they leave the
/// collection loops so no native handle is held by the UI.
/// </summary>
public sealed class PeopleInSpaceViewModel : INotifyPropertyChanged, IAsyncDisposable
{
    private static readonly List<PersonViewModel> NoPeople = [];

    private readonly PeopleInSpaceClient _client;
    private readonly DispatcherQueue _dispatcher;
    private readonly CancellationTokenSource _lifetime = new();
    private readonly RelayCommand _refreshCommand;
    private Task? _watchers;
    private string _issLocation = "Waiting for ISS position…";
    private string? _peopleError;
    private bool _peopleLoading = true;
    private bool _issLoading = true;
    private bool _isRefreshing;
    private PersonViewModel? _selectedPerson;

    public PeopleInSpaceViewModel(string storageDirectory, DispatcherQueue dispatcher)
    {
        _client = new PeopleInSpaceClient(storageDirectory);
        _dispatcher = dispatcher;
        _refreshCommand = new RelayCommand(() => _ = RefreshAsync(), () => !IsRefreshing);
    }

    public ObservableCollection<PersonViewModel> People { get; } = [];
    public ICommand RefreshCommand => _refreshCommand;
    public string IssLocation { get => _issLocation; private set => SetField(ref _issLocation, value); }
    public bool PeopleLoading { get => _peopleLoading; private set => SetField(ref _peopleLoading, value); }
    public bool IssLoading { get => _issLoading; private set => SetField(ref _issLoading, value); }
    public PersonViewModel? SelectedPerson { get => _selectedPerson; set => SetField(ref _selectedPerson, value); }
    public bool HasPeopleError => !string.IsNullOrWhiteSpace(PeopleError);

    public bool IsRefreshing
    {
        get => _isRefreshing;
        private set { if (SetField(ref _isRefreshing, value)) _refreshCommand.RaiseCanExecuteChanged(); }
    }

    public string? PeopleError
    {
        get => _peopleError;
        private set { if (SetField(ref _peopleError, value)) OnPropertyChanged(nameof(HasPeopleError)); }
    }

    public event PropertyChangedEventHandler? PropertyChanged;

    /// <summary>Starts collecting both flows; safe to call more than once.</summary>
    public void Start() => _watchers ??= Task.WhenAll(WatchPeopleAsync(), WatchIssAsync());

    public async Task RefreshAsync()
    {
        OnUi(() => PeopleError = null);
        try { await _client.RefreshAsync(_lifetime.Token).ConfigureAwait(false); }
        catch (OperationCanceledException) { }
        catch (Exception exception) { OnUi(() => PeopleError = exception.Message); }
    }

    private async Task WatchPeopleAsync()
    {
        try
        {
            using var flow = _client.PeopleState;
            await foreach (var state in flow.WithCancellation(_lifetime.Token).ConfigureAwait(false))
            {
                using (state)
                {
                    (List<PersonViewModel> people, bool loading, bool refreshing, string? error) = state switch
                    {
                        PersonListUiState.Success success => (Project(success.Result), false, success.Refreshing, null),
                        PersonListUiState.Error failure => (NoPeople, false, false, failure.Message),
                        _ => (NoPeople, true, false, null),
                    };
                    OnUi(() =>
                    {
                        ReplacePeople(people);
                        PeopleLoading = loading;
                        IsRefreshing = refreshing;
                        PeopleError = error;
                    });
                }
            }
        }
        catch (OperationCanceledException) { }
        catch (Exception exception) { OnUi(() => { PeopleLoading = false; PeopleError = exception.Message; }); }
    }

    private async Task WatchIssAsync()
    {
        try
        {
            using var flow = _client.IssState;
            await foreach (var state in flow.WithCancellation(_lifetime.Token).ConfigureAwait(false))
            {
                using (state)
                {
                    if (state is not IssPositionUiState.Success success) continue;
                    using var position = success.Position;
                    var location = FormattableString.Invariant($"{position.Latitude:F3}°, {position.Longitude:F3}°");
                    OnUi(() =>
                    {
                        IssLocation = location;
                        IssLoading = false;
                    });
                }
            }
        }
        catch (OperationCanceledException) { }
        catch (Exception exception) { OnUi(() => { IssLoading = false; IssLocation = exception.Message; }); }
    }

    private static List<PersonViewModel> Project(IReadOnlyList<Assignment> people) =>
        people.Select(Project).OrderBy(person => person.Name, StringComparer.OrdinalIgnoreCase).ToList();

    private static PersonViewModel Project(Assignment person)
    {
        using (person)
            return new PersonViewModel(person.Name, person.Craft, person.Nationality, person.PersonImageUrl, person.PersonBio);
    }

    private void ReplacePeople(List<PersonViewModel> people)
    {
        var selected = SelectedPerson;
        People.Clear();
        foreach (var person in people) People.Add(person);
        SelectedPerson = selected is null ? null : People.FirstOrDefault(person => person.Name == selected.Name && person.Craft == selected.Craft);
    }

    private void OnUi(Action action)
    {
        if (_dispatcher.HasThreadAccess) action();
        else _dispatcher.TryEnqueue(() => action());
    }

    public async ValueTask DisposeAsync()
    {
        _lifetime.Cancel();
        if (_watchers is { } watchers)
        {
            try { await watchers.ConfigureAwait(false); }
            catch (OperationCanceledException) { }
        }
        _client.Close();
        await _client.DisposeAsync().ConfigureAwait(false);
        _lifetime.Dispose();
    }

    private bool SetField<T>(ref T field, T value, [CallerMemberName] string? name = null)
    {
        if (EqualityComparer<T>.Default.Equals(field, value)) return false;
        field = value;
        OnPropertyChanged(name);
        return true;
    }

    private void OnPropertyChanged(string? name) => PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(name));

    private sealed class RelayCommand(Action execute, Func<bool> canExecute) : ICommand
    {
        public event EventHandler? CanExecuteChanged;
        public bool CanExecute(object? parameter) => canExecute();
        public void Execute(object? parameter) => execute();
        public void RaiseCanExecuteChanged() => CanExecuteChanged?.Invoke(this, EventArgs.Empty);
    }
}
