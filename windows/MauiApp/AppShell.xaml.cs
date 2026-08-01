using Microsoft.Extensions.DependencyInjection;

namespace PeopleInSpace.Windows.MauiApp;

public partial class AppShell : TabbedPage
{
    private readonly IServiceProvider _services;

    public AppShell(IServiceProvider services)
    {
        InitializeComponent();
        _services = services;
    }

    public void InitializePages()
    {
        if (Children.Count != 0) return;
        var peoplePage = _services.GetRequiredService<PeoplePage>();
        var issPage = _services.GetRequiredService<IssPage>();
        peoplePage.Title = "People";
        issPage.Title = "ISS";
        Children.Add(peoplePage);
        Children.Add(issPage);
    }

    public void DisconnectPageHandlers()
    {
        foreach (var page in Children)
            page.Handler?.DisconnectHandler();
        Handler?.DisconnectHandler();
    }
}
