namespace PeopleInSpace.Windows.Shared;

/// <summary>
/// Application-facing projection of Kotlin's two StateFlows. It deliberately contains no WinUI
/// types, so tests can use a fake source without loading Kotlin native code or using the network.
/// </summary>
public interface IPeopleInSpaceSource : IAsyncDisposable
{
    IAsyncEnumerable<PeopleSnapshot> WatchPeopleAsync(CancellationToken cancellationToken);

    IAsyncEnumerable<IssSnapshot> WatchIssAsync(CancellationToken cancellationToken);

    Task RefreshAsync(CancellationToken cancellationToken);
}
