using PeopleInSpace.Kotlin;

namespace PeopleInSpace.Windows.Shared;

/// <summary>
/// Owns the generated Kotlin client and projects its StateFlows into managed snapshots. Generated
/// objects are copied and disposed before they leave this boundary, preventing native-handle leaks.
/// </summary>
public sealed class KotlinPeopleInSpaceSource : IPeopleInSpaceSource
{
    // kotlin-native-nuget 0.2.0's generic flow collector uses runtime-generated
    // reverse-P/Invoke delegates, which Mac Catalyst cannot JIT in AOT-only mode.
    // Reading StateFlow.Value keeps the same Kotlin-owned state and is AOT-safe.
    private static readonly TimeSpan StatePollInterval = TimeSpan.FromMilliseconds(250);
    private PeopleInSpaceClient? _client;

    public KotlinPeopleInSpaceSource(string storageDirectory) =>
        _client = new PeopleInSpaceClient(storageDirectory);

    public async IAsyncEnumerable<PeopleSnapshot> WatchPeopleAsync(
        [System.Runtime.CompilerServices.EnumeratorCancellation] CancellationToken cancellationToken)
    {
        using var flow = Client.PeopleState;
        PeopleSnapshot? previous = null;
        while (true)
        {
            cancellationToken.ThrowIfCancellationRequested();
            using var state = flow.Value;
            var snapshot = Project(state);
            if (previous is null || !PeopleSnapshotsEqual(previous, snapshot))
            {
                previous = snapshot;
                yield return snapshot;
            }
            await Task.Delay(StatePollInterval, cancellationToken).ConfigureAwait(false);
        }
    }

    public async IAsyncEnumerable<IssSnapshot> WatchIssAsync(
        [System.Runtime.CompilerServices.EnumeratorCancellation] CancellationToken cancellationToken)
    {
        using var flow = Client.IssState;
        IssSnapshot? previous = null;
        while (true)
        {
            cancellationToken.ThrowIfCancellationRequested();
            using var state = flow.Value;
            var snapshot = new IssSnapshot(
                state.Latitude,
                state.Longitude,
                DateTimeOffset.FromUnixTimeSeconds(state.Timestamp),
                state.HasPosition,
                state.Loading,
                state.ErrorMessage);
            if (snapshot != previous)
            {
                previous = snapshot;
                yield return snapshot;
            }
            await Task.Delay(StatePollInterval, cancellationToken).ConfigureAwait(false);
        }
    }

    public Task RefreshAsync(CancellationToken cancellationToken)
    {
        cancellationToken.ThrowIfCancellationRequested();
        Client.RequestRefresh();
        return Task.CompletedTask;
    }

    public async ValueTask DisposeAsync()
    {
        var client = Interlocked.Exchange(ref _client, null);
        if (client is null) return;
        try { client.Close(); }
        finally { client.Dispose(); }
        await ValueTask.CompletedTask;
    }

    private PeopleInSpaceClient Client => _client ?? throw new ObjectDisposedException(nameof(KotlinPeopleInSpaceSource));

    private static PeopleSnapshot Project(PeopleState state)
    {
        var people = state.People;
        try
        {
            return new PeopleSnapshot(
                people.Select(person => new PersonInfo(
                    person.Name,
                    person.Craft,
                    person.Nationality,
                    person.PersonImageUrl,
                    person.PersonBio)).ToArray(),
                state.InitialLoading,
                state.Refreshing,
                state.ErrorMessage);
        }
        finally
        {
            foreach (var person in people)
                person.Dispose();
        }
    }

    private static bool PeopleSnapshotsEqual(PeopleSnapshot left, PeopleSnapshot right) =>
        left.IsInitialLoading == right.IsInitialLoading &&
        left.IsRefreshing == right.IsRefreshing &&
        left.Error == right.Error &&
        left.People.SequenceEqual(right.People);
}
