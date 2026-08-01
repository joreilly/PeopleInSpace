using PeopleInSpace.Windows.Shared;

namespace PeopleInSpace.Windows.MauiApp;

public partial class PeoplePage : ContentPage
{
    private readonly PeopleInSpaceViewModel _viewModel;

    public PeoplePage(PeopleInSpaceViewModel viewModel)
    {
        InitializeComponent();
        BindingContext = _viewModel = viewModel;
        Loaded += OnLoaded;
    }

    private async void OnLoaded(object? sender, EventArgs eventArgs) =>
        await _viewModel.StartAsync();
}
