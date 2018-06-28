package org.fundaciobit.plugins.utils.cxf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;

/**
 * 
 * @author anadal
 * 
 */
public class ClientHandlerUsernamePassword extends ClientHandler {

  /**
   * 
   */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = -2810301885896179645L;
  private final String username;
  private final String password;

  /**
   * @param username
   * @param password
   */
  public ClientHandlerUsernamePassword(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  protected void internalAddSecureHeader(Object api) {

    org.apache.cxf.endpoint.Client client = ClientProxy.getClient(api);
    org.apache.cxf.endpoint.Endpoint cxfEndpoint = client.getEndpoint();

    Map<String, Object> outProps = new HashMap<String, Object>();

    outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
    // Specify our username
    outProps.put(WSHandlerConstants.USER, username);

    // for hashed password use:
    outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
    // Password type : plain text
    // outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);

    outProps.put(WSHandlerConstants.MUST_UNDERSTAND, "false");

    outProps.put(WSHandlerConstants.ADD_UT_ELEMENTS, WSConstants.NONCE_LN + " "
        + WSConstants.CREATED_LN);

    outProps.put("addUsernameTokenNonce", "true");
    outProps.put("addUsernameTokenCreated", "true");

    // Callback used to retrieve password for given user.
    outProps.put(WSHandlerConstants.PW_CALLBACK_REF, new ClientPasswordCallback(password));

    WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
    cxfEndpoint.getOutInterceptors().add(wssOut);

  }

  public static class ClientPasswordCallback implements CallbackHandler {
    final String password;

    /**
     * @param password
     */
    public ClientPasswordCallback(String password) {
      super();
      this.password = password;
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

      WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];

      // set the password for our message.
      pc.setPassword(password);
    }

  }

}
