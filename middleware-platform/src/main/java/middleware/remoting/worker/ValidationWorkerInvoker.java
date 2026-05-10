package middleware.remoting.worker;

import middleware.remoting.InvocationContext;
import middleware.remoting.Invoker;
import middleware.remoting.extension.InterceptorChain;
import middleware.remoting.marshal.ValidationSemicolonMarshaller;

/**
 * Despacho validação: três inteiros → operação {@code validar}.
 */
public final class ValidationWorkerInvoker {

    private ValidationWorkerInvoker() {}

    public static Object invoke(
            String payload,
            InvocationContext ctx,
            Invoker invoker,
            ValidationSemicolonMarshaller marshaller,
            InterceptorChain chain) throws Exception {

        ValidationSemicolonMarshaller.ValidationParts parts = marshaller.parse(payload);
        return chain.execute(ctx,
                () -> invoker.invoke(ctx, "validar", new Object[]{parts.valor(), parts.minimo(), parts.maximo()}));
    }
}
