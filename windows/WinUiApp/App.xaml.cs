using Microsoft.UI.Dispatching;
using Microsoft.UI.Xaml;

namespace PeopleInSpace.Windows.WinUiApp;

public partial class App : Application
{
    private Window? _window;

    public App() => InitializeComponent();

    protected override void OnLaunched(LaunchActivatedEventArgs args)
    {
        var queue = DispatcherQueue.GetForCurrentThread();
        // Unpackaged apps cannot use Windows.Storage.ApplicationData, so keep the database under %LOCALAPPDATA%.
        var storageDirectory = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "PeopleInSpace");
        Directory.CreateDirectory(storageDirectory);
        var viewModel = new PeopleInSpaceViewModel(storageDirectory, queue);
        _window = new MainWindow(viewModel);
        _window.Activate();
    }
}
