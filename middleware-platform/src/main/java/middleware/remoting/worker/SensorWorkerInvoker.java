package middleware.remoting.worker;

import middleware.remoting.InvocationContext;
import middleware.remoting.Invoker;
import middleware.remoting.extension.InterceptorChain;
import middleware.remoting.marshal.SensorSemicolonMarshaller;

/**
 * Despacho sensor: payload textual → Invoker → resposta textual (Fases B–C).
 */
public final class SensorWorkerInvoker {

    private SensorWorkerInvoker() {}

    public static String invoke(
            String payload,
            InvocationContext ctx,
            Invoker invoker,
            SensorSemicolonMarshaller marshaller,
            InterceptorChain chain) throws Exception {

        SensorSemicolonMarshaller.SensorParts parts = marshaller.parse(payload);
        Object result = chain.execute(ctx, () -> invoker.invoke(ctx, parts.operation(), new Object[0]));
        String numeric = String.valueOf(result);
        return marshaller.formatResult(numeric, parts.field2(), parts.field3());
    }
}
