using Microsoft.UI.Composition.SystemBackdrops;
using Microsoft.UI.Xaml;
using Microsoft.UI.Xaml.Media;

namespace PeopleInSpace.Windows.WinUiApp;

public sealed class MainWindow : Window
{
    private readonly PeopleInSpaceViewModel _viewModel;

    public MainWindow(PeopleInSpaceViewModel viewModel, LocalAppDataStore store)
    {
        _viewModel = viewModel;
        Title = "People in Space";
        SystemBackdrop = new MicaBackdrop { Kind = MicaKind.BaseAlt };
        Content = new MainPage(viewModel, store);
        Closed += OnClosed;
    }

    private async void OnClosed(object sender, WindowEventArgs args)
    {
        Closed -= OnClosed;
        await _viewModel.DisposeAsync();
    }
}
