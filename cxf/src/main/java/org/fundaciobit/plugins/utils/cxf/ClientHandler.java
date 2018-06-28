package org.fundaciobit.plugins.utils.cxf;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

/**
 * 
 * @author anadal
 *
 */
public abstract class ClientHandler {

  public final void addSecureHeader(Object api) {
    // @firma no suporta. Veure https://github.com/GovernIB/pluginsib/issues/3
    Client client = ClientProxy.getClient(api);
    {
      HTTPConduit conduit = (HTTPConduit) client.getConduit();
      HTTPClientPolicy policy = new HTTPClientPolicy();
      policy.setAllowChunking(false);
      conduit.setClient(policy);
    }

    internalAddSecureHeader(api);
  }

  protected abstract void internalAddSecureHeader(Object api);

}
