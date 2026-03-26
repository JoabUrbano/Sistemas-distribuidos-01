package ApiGateway.Comunicacao.Template;

import Shared.Service;
import java.sql.Timestamp;

import ApiGateway.Comunicacao.ServerContract;

public abstract class ServerTemplate implements ServerContract {
	private Service[] servicesValidacao = new Service[0];
	private Service[] servicesSensoriamento = new Service[0];


	public void start() {
        System.out.println("Server Started");
	}

	public void addServiceValidacao(Service service) {
		for (Service s : servicesValidacao) {
			if (s.getName().equals(service.getName()) && s.getPort().equals(service.getPort())) {
				s.setUltimoHeartBeat(new Timestamp(System.currentTimeMillis()));
				System.out.println("Serviço: "+s.getUrl()+" atualizado " + s.getType());
				return;
			}
		}
		System.out.println("Serviço: "+service.getUrl()+" adicionado");
		
		Service[] newServices = new Service[servicesValidacao.length + 1];
		for (int i = 0; i < servicesValidacao.length; i++) {
			newServices[i] = servicesValidacao[i];
		}
		newServices[servicesValidacao.length] = service;
		servicesValidacao = newServices;
	}

	public void addServiceSensoriamento(Service service) {
		for (Service s : servicesSensoriamento) {
			if (s.getName().equals(service.getName()) && s.getPort().equals(service.getPort())) {
				s.setUltimoHeartBeat(new Timestamp(System.currentTimeMillis()));
				System.out.println("Serviço: "+s.getUrl()+" atualizado " + s.getType());
				return;
			}
		}
		System.out.println("Serviço: "+service.getUrl()+" adicionado");
		
		Service[] newServices = new Service[servicesSensoriamento.length + 1];
		for (int i = 0; i < servicesSensoriamento.length; i++) {
			newServices[i] = servicesSensoriamento[i];
		}
		newServices[servicesSensoriamento.length] = service;
		servicesSensoriamento = newServices;
	}

	public void removeServiceValidacao(Service service) {
		Service[] newServices = new Service[servicesValidacao.length - 1];
		for (int i = 0; i < servicesValidacao.length; i++) {
			if (servicesValidacao[i].getName().equals(service.getName())) {
				continue;
			}
			newServices[i] = servicesValidacao[i];
		}
		servicesValidacao = newServices;
	}

	public void removeServiceSensoriamento(Service service) {
		Service[] newServices = new Service[servicesSensoriamento.length - 1];
		for (int i = 0; i < servicesSensoriamento.length; i++) {
			if (servicesSensoriamento[i].getName().equals(service.getName())) {
				continue;
			}
			newServices[i] = servicesSensoriamento[i];
		}
		servicesSensoriamento = newServices;
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

	public void clearServicesSensoriamento() {
		servicesSensoriamento = new Service[0];
	}

	public Service[] getServicesValidacao() {
		return servicesValidacao;
	}

	public Service[] getServicesSensoriamento() {
		return servicesSensoriamento;
	}
}
