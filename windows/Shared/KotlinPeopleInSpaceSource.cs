using PeopleInSpace.Kotlin;

namespace PeopleInSpace.Windows.Shared;

/// <summary>
/// Owns the generated Kotlin client and collects its StateFlows into managed snapshots. Generated
/// objects are copied and disposed before they leave this boundary, preventing native-handle leaks.
/// </summary>
public sealed class KotlinPeopleInSpaceSource : IPeopleInSpaceSource
{
    private PeopleInSpaceClient? _client;

    public KotlinPeopleInSpaceSource(string storageDirectory) =>
        _client = new PeopleInSpaceClient(storageDirectory);

    public async IAsyncEnumerable<PeopleSnapshot> WatchPeopleAsync(
        [System.Runtime.CompilerServices.EnumeratorCancellation] CancellationToken cancellationToken)
    {
        using var flow = Client.PeopleState;
        await foreach (var state in flow.WithCancellation(cancellationToken).ConfigureAwait(false))
        {
            using (state)
                yield return Project(state);
        }
    }

    public async IAsyncEnumerable<IssSnapshot> WatchIssAsync(
        [System.Runtime.CompilerServices.EnumeratorCancellation] CancellationToken cancellationToken)
    {
        using var flow = Client.IssState;
        await foreach (var state in flow.WithCancellation(cancellationToken).ConfigureAwait(false))
        {
            using (state)
                yield return new IssSnapshot(
                    state.Latitude,
                    state.Longitude,
                    state.Timestamp,
                    state.HasPosition,
                    state.Loading,
                    state.ErrorMessage);
        }
    }

    public Task RefreshAsync(CancellationToken cancellationToken) => Client.RefreshAsync(cancellationToken);

    public async ValueTask DisposeAsync()
    {
        var client = Interlocked.Exchange(ref _client, null);
        if (client is null) return;
        try { client.Close(); }
        finally { await client.DisposeAsync().ConfigureAwait(false); }
    }

    private PeopleInSpaceClient Client => _client ?? throw new ObjectDisposedException(nameof(KotlinPeopleInSpaceSource));

    private static PeopleSnapshot Project(PeopleState state)
    {
        var people = state.People;
        var projected = new PersonInfo[people.Count];
        for (var index = 0; index < people.Count; index++)
        {
            using var person = people[index];
            projected[index] = new PersonInfo(
                person.Name,
                person.Craft,
                person.Nationality,
                person.PersonImageUrl,
                person.PersonBio);
        }

        return new PeopleSnapshot(projected, state.InitialLoading, state.Refreshing, state.ErrorMessage);
    }
}
