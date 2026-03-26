package ApiGateway.Comunicacao.Template;

import Shared.Service;
import java.sql.Timestamp;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import ApiGateway.Comunicacao.ServerContract;

public abstract class ServerTemplate implements ServerContract {
	private CopyOnWriteArrayList<Service> servicesValidacao = new CopyOnWriteArrayList<>();
	private CopyOnWriteArrayList<Service> servicesSensoriamento = new CopyOnWriteArrayList<>();

	public void start() {
        System.out.println("Server Started");
	}

	public void addServiceValidacao(Service service) {
		for (Service s : servicesValidacao) {
			if (s.getName().equals(service.getName()) &&
				s.getPort().equals(service.getPort())) {
	
				s.setUltimoHeartBeat(new Timestamp(System.currentTimeMillis()));
				return;
			}
		}
	
		System.out.println("Serviço: " + service.getUrl() + " adicionado");
		servicesValidacao.add(service);
	}

	public void addServiceSensoriamento(Service service) {
		for (Service s : servicesSensoriamento) {
			if (s.getName().equals(service.getName()) &&
				s.getPort().equals(service.getPort())) {
	
				s.setUltimoHeartBeat(new Timestamp(System.currentTimeMillis()));
				return;
			}
		}
	
		System.out.println("Serviço: " + service.getUrl() + " adicionado");
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
						if(s.getUltimoHeartBeat().before(new Timestamp(System.currentTimeMillis() - 5000))) {
							removeServiceValidacao(s);
							System.out.println("Serviço: "+s.getUrl()+" removido");
						}
					}
					for (Service s : servicesSensoriamento) {
						if(s.getUltimoHeartBeat().before(new Timestamp(System.currentTimeMillis() - 5000))) {
							removeServiceSensoriamento(s);
							System.out.println("Serviço: "+s.getUrl()+" removido");
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
		if (servicesValidacao.isEmpty()) return null;

		int index = ThreadLocalRandom.current().nextInt(servicesValidacao.size());
		return servicesValidacao.get(index);
	}

	public Service getRandomServiceSensoriamento() {
		if (servicesSensoriamento.isEmpty()) return null;

		int index = ThreadLocalRandom.current().nextInt(servicesSensoriamento.size());
		return servicesSensoriamento.get(index);
	}
}
