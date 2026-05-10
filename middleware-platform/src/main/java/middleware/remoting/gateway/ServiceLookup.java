package middleware.remoting.gateway;

import middleware.remoting.identification.AbsoluteObjectReference;
import middleware.shared.Service;

/**
 * Lookup (Fase D): visão de consulta sobre serviços registrados no gateway.
 */
public interface ServiceLookup {

    Service pickRandomSensor();

    Service pickRandomValidacao();

    default AbsoluteObjectReference pickSensorAor() {
        Service s = pickRandomSensor();
        return s == null ? null : s.toAbsoluteReference();
    }

    default AbsoluteObjectReference pickValidacaoAor() {
        Service s = pickRandomValidacao();
        return s == null ? null : s.toAbsoluteReference();
    }
}
