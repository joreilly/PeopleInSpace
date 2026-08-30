using Microsoft.UI.Xaml;
using Microsoft.UI.Xaml.Controls;
using Microsoft.UI.Xaml.Data;
using Microsoft.UI.Xaml.Media;
using Microsoft.UI.Xaml.Media.Imaging;

namespace PeopleInSpace.Windows.WinUiApp;

public sealed partial class MainPage : Page
{
    private readonly LocalAppDataStore _store;
    private readonly CancellationTokenSource _lifetime = new();

    public MainPage(PeopleInSpaceViewModel viewModel, LocalAppDataStore store)
    {
        ViewModel = viewModel;
        _store = store;
        DataContext = ViewModel;
        InitializeComponent();
        Loaded += OnLoaded;
        Unloaded += OnUnloaded;
    }

    public PeopleInSpaceViewModel ViewModel { get; }

    private async void OnLoaded(object sender, RoutedEventArgs args)
    {
        try
        {
            _ = await _store.LoadLastOpenedAsync(_lifetime.Token);
            await _store.SaveLastOpenedAsync(DateTimeOffset.UtcNow, _lifetime.Token);
            ViewModel.Start();
        }
        catch (OperationCanceledException) { }
    }

    private void OnUnloaded(object sender, RoutedEventArgs args)
    {
        Loaded -= OnLoaded;
        Unloaded -= OnUnloaded;
        _lifetime.Cancel();
        _lifetime.Dispose();
    }

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
