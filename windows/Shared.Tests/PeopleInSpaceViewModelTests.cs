using System.Threading.Channels;
using PeopleInSpace.Windows.Shared;
using Xunit;

namespace PeopleInSpace.Windows.Shared.Tests;

public sealed class PeopleInSpaceViewModelTests
{
    [Fact]
    public async Task Projects_people_and_iss_snapshots_to_bindable_properties()
    {
        var source = new FakeSource();
        var viewModel = new PeopleInSpaceViewModel(source, new RecordingDispatcher());
        await StartAsync(source, viewModel);

        source.PublishPeople(People(
            new PersonInfo("Zhenya Kondratiev", "ISS"),
            new PersonInfo("Ada Lovelace", "ISS", "British")));
        source.PublishIss(Iss(6.921, 79.861));

        await WaitUntilAsync(() => viewModel.People.Count == 2 && viewModel.HasIssPosition);
        Assert.Collection(viewModel.People,
            person => Assert.Equal("Ada Lovelace", person.Name),
            person => Assert.Equal("Zhenya Kondratiev", person.Name));
        Assert.Equal("6.921°, 79.861°", viewModel.IssLocation);
        Assert.Equal(DateTimeOffset.UnixEpoch, viewModel.IssTimestamp);

        await viewModel.DisposeAsync();
    }

    [Fact]
    public async Task Replaces_people_and_preserves_the_selected_person_by_identity()
    {
        var source = new FakeSource();
        var viewModel = new PeopleInSpaceViewModel(source, new RecordingDispatcher());
        await StartAsync(source, viewModel);
        source.PublishPeople(People(new PersonInfo("Ada", "ISS", Biography: "First bio")));
        await WaitUntilAsync(() => viewModel.People.Count == 1);
        viewModel.SelectedPerson = viewModel.People.Single();

        source.PublishPeople(People(
            new PersonInfo("Ada", "ISS", Biography: "Updated bio"),
            new PersonInfo("Zhenya", "ISS")));
        await WaitUntilAsync(() => viewModel.People.Count == 2 && viewModel.SelectedPerson?.Biography == "Updated bio");

        Assert.Equal("Ada", viewModel.SelectedPerson?.Name);
        Assert.Equal("Updated bio", viewModel.SelectedPerson?.Biography);
        await viewModel.DisposeAsync();
    }

    [Fact]
    public async Task A_clean_state_clears_errors_and_refresh_only_changes_people_error()
    {
        var source = new FakeSource();
        var viewModel = new PeopleInSpaceViewModel(source, new RecordingDispatcher());
        await StartAsync(source, viewModel);
        source.PublishPeople(People(error: "Offline"));
        source.PublishIss(Iss(error: "Offline"));
        await WaitUntilAsync(() => viewModel.HasPeopleError && viewModel.HasIssError);

        source.PublishPeople(People());
        await WaitUntilAsync(() => !viewModel.HasPeopleError);
        var before = source.RefreshCount;
        viewModel.RefreshCommand.Execute(null);
        await WaitUntilAsync(() => source.RefreshCount == before + 1);

        Assert.Equal(before + 1, source.RefreshCount);
        Assert.Equal("Offline", viewModel.IssError);
        await viewModel.DisposeAsync();
    }

    [Fact]
    public async Task Dispatches_state_changes_and_surfaces_refresh_errors()
    {
        var source = new FakeSource { RefreshException = new InvalidOperationException("Service unavailable") };
        var dispatcher = new RecordingDispatcher();
        var viewModel = new PeopleInSpaceViewModel(source, dispatcher);
        var canExecuteChanged = 0;
        var propertyChangedOnDispatcher = true;
        viewModel.PropertyChanged += (_, _) => propertyChangedOnDispatcher &= dispatcher.IsInvoking;
        viewModel.RefreshCommand.CanExecuteChanged += (_, _) => canExecuteChanged++;
        await StartAsync(source, viewModel);
        source.PublishPeople(People(false, true));
        await WaitUntilAsync(() => viewModel.IsRefreshing && canExecuteChanged > 0);
        Assert.False(viewModel.RefreshCommand.CanExecute(null));
        source.PublishPeople(People());
        await WaitUntilAsync(() => dispatcher.InvocationCount >= 2);
        await viewModel.RefreshAsync();

        Assert.True(dispatcher.InvocationCount >= 4);
        Assert.True(propertyChangedOnDispatcher);
        Assert.Equal("Service unavailable", viewModel.PeopleError);
        Assert.Null(viewModel.IssError);
        await viewModel.DisposeAsync();
    }

    [Fact]
    public async Task Dispose_cancels_collection_and_disposes_the_source()
    {
        var source = new FakeSource();
        var viewModel = new PeopleInSpaceViewModel(source, new RecordingDispatcher());
        await StartAsync(source, viewModel);

        await viewModel.DisposeAsync();

        Assert.True(source.PeopleCancelled);
        Assert.True(source.IssCancelled);
        Assert.True(source.Disposed);
    }

    [Fact]
    public async Task Dispose_cancels_and_awaits_an_active_refresh_before_disposing_the_source()
    {
        var source = new FakeSource { WaitForRefreshCancellation = true };
        var viewModel = new PeopleInSpaceViewModel(source, new RecordingDispatcher());
        await StartAsync(source, viewModel);

        var refresh = viewModel.RefreshAsync();
        await source.RefreshStarted.Task.WaitAsync(TimeSpan.FromSeconds(1));
        await viewModel.DisposeAsync();
        await refresh;

        Assert.True(source.RefreshCancelled);
        Assert.True(source.DisposedAfterRefreshCancelled);
    }

    private static async Task StartAsync(FakeSource source, PeopleInSpaceViewModel viewModel)
    {
        await viewModel.StartAsync();
        await source.PeopleObserved.Task.WaitAsync(TimeSpan.FromSeconds(1));
        await source.IssObserved.Task.WaitAsync(TimeSpan.FromSeconds(1));
    }

    private static PeopleSnapshot People(params PersonInfo[] people) => new(people, false, false, null);

    private static PeopleSnapshot People(bool isInitialLoading, bool isRefreshing) =>
        new([], isInitialLoading, isRefreshing, null);

    private static PeopleSnapshot People(string? error) => new([], false, false, error);

    private static IssSnapshot Iss(double latitude = 0, double longitude = 0, string? error = null) =>
        new(latitude, longitude, DateTimeOffset.UnixEpoch, error is null, false, error);

    private static async Task WaitUntilAsync(Func<bool> predicate)
    {
        var timeout = DateTimeOffset.UtcNow.AddSeconds(1);
        while (!predicate())
        {
            if (DateTimeOffset.UtcNow >= timeout) throw new TimeoutException();
            await Task.Delay(10);
        }
    }

    private sealed class RecordingDispatcher : IUiDispatcher
    {
        public int InvocationCount { get; private set; }
        public bool IsInvoking { get; private set; }

        public Task InvokeAsync(Action action, CancellationToken cancellationToken = default)
        {
            cancellationToken.ThrowIfCancellationRequested();
            InvocationCount++;
            IsInvoking = true;
            try { action(); }
            finally { IsInvoking = false; }
            return Task.CompletedTask;
        }
    }

    private sealed class FakeSource : IPeopleInSpaceSource
    {
        private readonly Channel<PeopleSnapshot> _people = Channel.CreateUnbounded<PeopleSnapshot>();
        private readonly Channel<IssSnapshot> _iss = Channel.CreateUnbounded<IssSnapshot>();

        public TaskCompletionSource PeopleObserved { get; } = new(TaskCreationOptions.RunContinuationsAsynchronously);
        public TaskCompletionSource IssObserved { get; } = new(TaskCreationOptions.RunContinuationsAsynchronously);
        public TaskCompletionSource RefreshStarted { get; } = new(TaskCreationOptions.RunContinuationsAsynchronously);
        public Exception? RefreshException { get; init; }
        public bool WaitForRefreshCancellation { get; init; }
        public int RefreshCount { get; private set; }
        public bool PeopleCancelled { get; private set; }
        public bool IssCancelled { get; private set; }
        public bool RefreshCancelled { get; private set; }
        public bool Disposed { get; private set; }
        public bool DisposedAfterRefreshCancelled { get; private set; }

        public void PublishPeople(PeopleSnapshot snapshot) => _people.Writer.TryWrite(snapshot);

        public void PublishIss(IssSnapshot snapshot) => _iss.Writer.TryWrite(snapshot);

        public async IAsyncEnumerable<PeopleSnapshot> WatchPeopleAsync(
            [System.Runtime.CompilerServices.EnumeratorCancellation] CancellationToken cancellationToken)
        {
            PeopleObserved.TrySetResult();
            try
            {
                while (await _people.Reader.WaitToReadAsync(cancellationToken))
                    while (_people.Reader.TryRead(out var snapshot)) yield return snapshot;
            }
            finally
            {
                PeopleCancelled = cancellationToken.IsCancellationRequested;
            }
        }

        public async IAsyncEnumerable<IssSnapshot> WatchIssAsync(
            [System.Runtime.CompilerServices.EnumeratorCancellation] CancellationToken cancellationToken)
        {
            IssObserved.TrySetResult();
            try
            {
                while (await _iss.Reader.WaitToReadAsync(cancellationToken))
                    while (_iss.Reader.TryRead(out var snapshot)) yield return snapshot;
            }
            finally
            {
                IssCancelled = cancellationToken.IsCancellationRequested;
            }
        }

        public async Task RefreshAsync(CancellationToken cancellationToken)
        {
            RefreshCount++;
            if (WaitForRefreshCancellation)
            {
                RefreshStarted.TrySetResult();
                try { await Task.Delay(Timeout.InfiniteTimeSpan, cancellationToken); }
                catch (OperationCanceledException)
                {
                    RefreshCancelled = true;
                    throw;
                }
            }

            if (RefreshException is not null) throw RefreshException;
        }

        public ValueTask DisposeAsync()
        {
            Disposed = true;
            DisposedAfterRefreshCancelled = RefreshCancelled;
            return ValueTask.CompletedTask;
        }
    }
}
