using PeopleInSpace.Windows.Shared;

namespace PeopleInSpace.Windows.MauiApp;

public partial class App : Application
{
    private readonly AppShell _shell;
    private readonly PeopleInSpaceViewModel _viewModel;

    public App(AppShell shell, PeopleInSpaceViewModel viewModel)
    {
        InitializeComponent();
        _shell = shell;
        _viewModel = viewModel;
    }

    protected override Window CreateWindow(IActivationState? activationState)
    {
        _shell.InitializePages();
        return new Window(_shell)
        {
            Title = "People in Space",
            Width = 1200,
            Height = 780,
            MinimumWidth = 800,
            MinimumHeight = 600
        }.WithDestroyHandler(OnWindowDestroying);
    }

    private async void OnWindowDestroying(object? sender, EventArgs eventArgs)
    {
        if (sender is Window window) window.Destroying -= OnWindowDestroying;
        await _viewModel.DisposeAsync();
    }
}

internal static class WindowExtensions
{
    public static Window WithDestroyHandler(this Window window, EventHandler handler)
    {
        window.Destroying += handler;
        return window;
    }
}
