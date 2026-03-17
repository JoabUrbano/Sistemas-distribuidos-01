package ApiGateway.Comunicacao.Template;

import Shared.Service;
import java.sql.Timestamp;

import ApiGateway.Comunicacao.ServerContract;

public abstract class ServerTemplate implements ServerContract {
	private Service[] services = new Service[0];


	public void start() {
        System.out.println("Server Started");
	}

	public void addService(Service service) {
	
		for (Service s : services) {
			if (s.getName().equals(service.getName()) && s.getPort().equals(service.getPort())) {
				s.setUltimoHeartBeat(new Timestamp(System.currentTimeMillis()));
				System.out.println("Serviço: "+s.getUrl()+" atualizado");
				System.out.println("Ultimo HeartBeat: "+s.getUltimoHeartBeat());
				return;
			}
		}
		System.out.println("Serviço: "+service.getUrl()+" adicionado");
		
		Service[] newServices = new Service[services.length + 1];
		for (int i = 0; i < services.length; i++) {
			newServices[i] = services[i];
		}
		newServices[services.length] = service;
		services = newServices;
	}

	public void removeService(Service service) {
		Service[] newServices = new Service[services.length - 1];
		for (int i = 0; i < services.length; i++) {
			if (services[i].getName().equals(service.getName())) {
				continue;
			}
			newServices[i] = services[i];
		}
		services = newServices;
	}

	public Service[] getServices() {
		return services;
	}
}
