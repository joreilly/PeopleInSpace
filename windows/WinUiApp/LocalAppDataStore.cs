using System.Text.Json;

namespace PeopleInSpace.Windows.WinUiApp;

/// <summary>
/// Stores window state under the user's local application data directory. The application is
/// unpackaged, so it cannot use <c>Windows.Storage.ApplicationData</c>, which requires package
/// identity.
/// </summary>
public sealed class LocalAppDataStore
{
    private const string FileName = "people-in-space-window.json";

    public LocalAppDataStore()
    {
        StorageDirectory = Path.Combine(
            Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "PeopleInSpace");
        Directory.CreateDirectory(StorageDirectory);
    }

    public string StorageDirectory { get; }

    private string FilePath => Path.Combine(StorageDirectory, FileName);

    public async Task SaveLastOpenedAsync(DateTimeOffset value, CancellationToken cancellationToken)
    {
        var json = JsonSerializer.Serialize(new WindowState(value));
        await File.WriteAllTextAsync(FilePath, json, cancellationToken);
    }

    public async Task<DateTimeOffset?> LoadLastOpenedAsync(CancellationToken cancellationToken)
    {
        if (!File.Exists(FilePath)) return null;
        var json = await File.ReadAllTextAsync(FilePath, cancellationToken);
        return JsonSerializer.Deserialize<WindowState>(json)?.LastOpened;
    }

    private sealed record WindowState(DateTimeOffset LastOpened);
}
