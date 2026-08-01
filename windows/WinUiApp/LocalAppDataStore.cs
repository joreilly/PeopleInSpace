using System.Text.Json;
using Windows.Storage;

namespace PeopleInSpace.Windows.WinUiApp;

public sealed class LocalAppDataStore
{
    private const string FileName = "people-in-space-window.json";

    public string StorageDirectory => ApplicationData.Current.LocalFolder.Path;

    public async Task SaveLastOpenedAsync(DateTimeOffset value, CancellationToken cancellationToken)
    {
        cancellationToken.ThrowIfCancellationRequested();
        var file = await ApplicationData.Current.LocalFolder.CreateFileAsync(
            FileName, CreationCollisionOption.ReplaceExisting);
        await FileIO.WriteTextAsync(file, JsonSerializer.Serialize(new WindowState(value)));
        cancellationToken.ThrowIfCancellationRequested();
    }

    public async Task<DateTimeOffset?> LoadLastOpenedAsync(CancellationToken cancellationToken)
    {
        cancellationToken.ThrowIfCancellationRequested();
        try
        {
            var file = await ApplicationData.Current.LocalFolder.GetFileAsync(FileName);
            var json = await FileIO.ReadTextAsync(file);
            return JsonSerializer.Deserialize<WindowState>(json)?.LastOpened;
        }
        catch (FileNotFoundException) { return null; }
    }

    private sealed record WindowState(DateTimeOffset LastOpened);
}
