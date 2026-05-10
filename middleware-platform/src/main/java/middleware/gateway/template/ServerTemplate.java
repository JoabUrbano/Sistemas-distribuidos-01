package middleware.gateway.template;

import java.sql.Timestamp;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import middleware.gateway.ServerContract;
import middleware.remoting.gateway.ServiceLookup;
import middleware.remoting.identification.ObjectIds;
import middleware.shared.Service;

public abstract class ServerTemplate implements ServerContract, ServiceLookup {

    private final CopyOnWriteArrayList<Service> servicesValidacao = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Service> servicesSensoriamento = new CopyOnWriteArrayList<>();

    private static void ensureObjectId(Service service) {
        if (service.getObjectId() == null || service.getObjectId().isBlank()) {
            service.setObjectId(ObjectIds.deterministic(
                    service.getName(),
                    Integer.parseInt(service.getPort()),
                    service.getType()).value());
        }
    }

    @Override
    public void start() {
        System.out.println("Server Started");
    }

    public void addServiceValidacao(Service service) {
        ensureObjectId(service);
        for (Service s : servicesValidacao) {
            if (s.getName().equals(service.getName())
                    && s.getPort().equals(service.getPort())) {

                s.setUltimoHeartBeat(new Timestamp(System.currentTimeMillis()));
                return;
            }
        }

        System.out.println("Serviço: " + service.getUrl() + " objectId=" + service.getObjectId() + " adicionado");
        servicesValidacao.add(service);
    }

    public void addServiceSensoriamento(Service service) {
        ensureObjectId(service);
        for (Service s : servicesSensoriamento) {
            if (s.getName().equals(service.getName())
                    && s.getPort().equals(service.getPort())) {

                s.setUltimoHeartBeat(new Timestamp(System.currentTimeMillis()));
                return;
            }
        }

        System.out.println("Serviço: " + service.getUrl() + " objectId=" + service.getObjectId() + " adicionado");
        servicesSensoriamento.add(service);
    }

    public void removeServiceValidacao(Service service) {
        servicesValidacao.removeIf(s ->
                s.getUrl().equals(service.getUrl())
        );
    }

    public void removeServiceSensoriamento(Service service) {
        servicesSensoriamento.removeIf(s ->
                s.getUrl().equals(service.getUrl())
        );
    }

    public void clearServices() {
        new Thread(() -> {
            try {
                while (true) {
                    for (Service s : servicesValidacao) {
                        if (s.getUltimoHeartBeat().before(new Timestamp(System.currentTimeMillis() - 5000))) {
                            removeServiceValidacao(s);
                            System.out.println("[leasing] Serviço expirado: " + s.getUrl() + " id=" + s.getObjectId());
                        }
                    }
                    for (Service s : servicesSensoriamento) {
                        if (s.getUltimoHeartBeat().before(new Timestamp(System.currentTimeMillis() - 5000))) {
                            removeServiceSensoriamento(s);
                            System.out.println("[leasing] Serviço expirado: " + s.getUrl() + " id=" + s.getObjectId());
                        }
                    }
                    Thread.sleep(3000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public CopyOnWriteArrayList<Service> getServicesValidacao() {
        return servicesValidacao;
    }

    public CopyOnWriteArrayList<Service> getServicesSensoriamento() {
        return servicesSensoriamento;
    }

    public Service getRandomServiceValidacao() {
        if (servicesValidacao.isEmpty()) {
            return null;
        }

        int index = ThreadLocalRandom.current().nextInt(servicesValidacao.size());
        return servicesValidacao.get(index);
    }

    public Service getRandomServiceSensoriamento() {
        if (servicesSensoriamento.isEmpty()) {
            return null;
        }

        int index = ThreadLocalRandom.current().nextInt(servicesSensoriamento.size());
        return servicesSensoriamento.get(index);
    }

    @Override
    public Service pickRandomSensor() {
        return getRandomServiceSensoriamento();
    }

    @Override
    public Service pickRandomValidacao() {
        return getRandomServiceValidacao();
    }
}
