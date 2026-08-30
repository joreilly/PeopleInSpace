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
        var store = new LocalAppDataStore();
        var viewModel = new PeopleInSpaceViewModel(store.StorageDirectory, queue);
        _window = new MainWindow(viewModel, store);
        _window.Activate();
    }
}
