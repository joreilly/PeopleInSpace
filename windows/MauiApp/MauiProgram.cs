using Microsoft.Extensions.Logging;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Maui.Storage;
using PeopleInSpace.Windows.Shared;

namespace PeopleInSpace.Windows.MauiApp;

public static class MauiProgram
{
    public static global::Microsoft.Maui.Hosting.MauiApp CreateMauiApp()
    {
        var builder = global::Microsoft.Maui.Hosting.MauiApp.CreateBuilder();
        builder.UseMauiApp<App>();
        builder.Services.AddSingleton<IUiDispatcher, MauiUiDispatcher>();
        builder.Services.AddSingleton<KotlinPeopleInSpaceSource>(_ => new KotlinPeopleInSpaceSource(FileSystem.AppDataDirectory));
        builder.Services.AddSingleton<IPeopleInSpaceSource>(services => services.GetRequiredService<KotlinPeopleInSpaceSource>());
        builder.Services.AddSingleton<PeopleInSpaceViewModel>();
        builder.Services.AddSingleton<AppShell>();
        builder.Services.AddTransient<PeoplePage>();
        builder.Services.AddTransient<IssPage>();

#if DEBUG
        builder.Logging.AddDebug();
#endif
        return builder.Build();
    }
}
