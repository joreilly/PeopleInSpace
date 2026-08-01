namespace PeopleInSpace.Windows.Shared;

public interface IUiDispatcher
{
    Task InvokeAsync(Action action, CancellationToken cancellationToken = default);
}

public sealed class SynchronizationContextUiDispatcher : IUiDispatcher
{
    private readonly SynchronizationContext? _context;

    public SynchronizationContextUiDispatcher(SynchronizationContext? context = null) =>
        _context = context ?? SynchronizationContext.Current;

    public Task InvokeAsync(Action action, CancellationToken cancellationToken = default)
    {
        cancellationToken.ThrowIfCancellationRequested();
        if (_context is null || SynchronizationContext.Current == _context)
        {
            action();
            return Task.CompletedTask;
        }

        var completion = new TaskCompletionSource(TaskCreationOptions.RunContinuationsAsynchronously);
        _context.Post(_ =>
        {
            if (cancellationToken.IsCancellationRequested)
                completion.TrySetCanceled(cancellationToken);
            else
            {
                try { action(); completion.TrySetResult(); }
                catch (Exception exception) { completion.TrySetException(exception); }
            }
        }, null);
        return completion.Task;
    }
}
