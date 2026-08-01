using System.Globalization;

namespace PeopleInSpace.Windows.MauiApp;

public sealed class NullToBoolConverter : IValueConverter
{
    public object Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
    {
        var present = value is not null;
        return string.Equals(parameter?.ToString(), "Invert", StringComparison.OrdinalIgnoreCase) ? !present : present;
    }

    public object ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture) =>
        throw new NotSupportedException();
}
