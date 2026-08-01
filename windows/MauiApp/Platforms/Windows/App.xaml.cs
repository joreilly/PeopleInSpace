namespace PeopleInSpace.Windows.MauiApp.WinUI;

public partial class App : MauiWinUIApplication
{
    public App() => InitializeComponent();

    protected override global::Microsoft.Maui.Hosting.MauiApp CreateMauiApp() => MauiProgram.CreateMauiApp();
}
