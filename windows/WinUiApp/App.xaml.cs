using Microsoft.UI.Dispatching;
using Microsoft.UI.Xaml;
using PeopleInSpace.Windows.Shared;

namespace PeopleInSpace.Windows.WinUiApp;

public partial class App : Application
{
    private Window? _window;

    public App() => InitializeComponent();

    protected override void OnLaunched(LaunchActivatedEventArgs args)
    {
        var queue = DispatcherQueue.GetForCurrentThread();
        SynchronizationContext.SetSynchronizationContext(new DispatcherQueueSynchronizationContext(queue));

        var dispatcher = new SynchronizationContextUiDispatcher(SynchronizationContext.Current);
        var store = new LocalAppDataStore();
        var source = new KotlinPeopleInSpaceSource(store.StorageDirectory);
        var viewModel = new PeopleInSpaceViewModel(source, dispatcher);
        _window = new MainWindow(viewModel, store);
        _window.Activate();
    }
}
