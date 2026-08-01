namespace PeopleInSpace.Windows.Shared;

public sealed record PersonInfo(
    string Name,
    string Craft,
    string? Nationality = null,
    string? ImageUrl = null,
    string? Biography = null);

public sealed record PeopleSnapshot(
    IReadOnlyList<PersonInfo> People,
    bool IsInitialLoading,
    bool IsRefreshing,
    string? Error);

public sealed record IssSnapshot(
    double Latitude,
    double Longitude,
    DateTimeOffset Timestamp,
    bool HasPosition,
    bool IsLoading,
    string? Error);
