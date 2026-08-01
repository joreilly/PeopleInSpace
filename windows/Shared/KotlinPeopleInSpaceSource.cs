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
    // Reading a captured snapshot through scalar accessors avoids both reverse callbacks and the
    // reflection used to create generated Kotlin object wrappers, neither of which is AOT-safe.
    private static readonly TimeSpan StatePollInterval = TimeSpan.FromMilliseconds(250);
    private PeopleInSpaceClient? _client;

    public KotlinPeopleInSpaceSource(string storageDirectory) =>
        _client = new PeopleInSpaceClient(storageDirectory);

    public async IAsyncEnumerable<PeopleSnapshot> WatchPeopleAsync(
        [System.Runtime.CompilerServices.EnumeratorCancellation] CancellationToken cancellationToken)
    {
        PeopleSnapshot? previous = null;
        while (true)
        {
            cancellationToken.ThrowIfCancellationRequested();
            var snapshot = Project(Client);
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
        IssSnapshot? previous = null;
        while (true)
        {
            cancellationToken.ThrowIfCancellationRequested();
            Client.CaptureIss();
            var snapshot = new IssSnapshot(
                Client.CapturedIssLatitude(),
                Client.CapturedIssLongitude(),
                DateTimeOffset.FromUnixTimeSeconds(Client.CapturedIssTimestamp()),
                Client.CapturedIssHasPosition(),
                Client.CapturedIssLoading(),
                Client.CapturedIssErrorMessage());
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

    private static PeopleSnapshot Project(PeopleInSpaceClient client)
    {
        var count = client.CapturePeople();
        var people = new PersonInfo[count];
        for (var index = 0; index < count; index++)
            people[index] = new PersonInfo(
                client.CapturedPersonName(index),
                client.CapturedPersonCraft(index),
                client.CapturedPersonNationality(index),
                client.CapturedPersonImageUrl(index),
                client.CapturedPersonBio(index));

        return new PeopleSnapshot(
            people,
            client.CapturedPeopleInitialLoading(),
            client.CapturedPeopleRefreshing(),
            client.CapturedPeopleErrorMessage());
    }

    private static bool PeopleSnapshotsEqual(PeopleSnapshot left, PeopleSnapshot right) =>
        left.IsInitialLoading == right.IsInitialLoading &&
        left.IsRefreshing == right.IsRefreshing &&
        left.Error == right.Error &&
        left.People.SequenceEqual(right.People);
}
