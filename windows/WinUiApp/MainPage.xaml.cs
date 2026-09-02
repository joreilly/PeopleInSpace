using Microsoft.UI.Xaml;
using Microsoft.UI.Xaml.Controls;
using Microsoft.UI.Xaml.Data;
using Microsoft.UI.Xaml.Media;
using Microsoft.UI.Xaml.Media.Imaging;

namespace PeopleInSpace.Windows.WinUiApp;

public sealed partial class MainPage : Page
{
    public MainPage(PeopleInSpaceViewModel viewModel)
    {
        ViewModel = viewModel;
        DataContext = ViewModel;
        InitializeComponent();
        Loaded += (_, _) => ViewModel.Start();
    }

    public PeopleInSpaceViewModel ViewModel { get; }

    private void OnNavigationItemInvoked(NavigationView sender, NavigationViewItemInvokedEventArgs args)
    {
        var showPeople = args.InvokedItemContainer?.Tag as string != "iss";
        PeopleSection.Visibility = showPeople ? Visibility.Visible : Visibility.Collapsed;
        IssSection.Visibility = showPeople ? Visibility.Collapsed : Visibility.Visible;
    }
}

public sealed class ImageUrlConverter : IValueConverter
{
    public object? Convert(object value, Type targetType, object parameter, string language) =>
        value is string { Length: > 0 } url && Uri.TryCreate(url, UriKind.Absolute, out var uri)
            ? new BitmapImage(uri)
            : null;

    public object ConvertBack(object value, Type targetType, object parameter, string language) =>
        throw new NotSupportedException();
}
