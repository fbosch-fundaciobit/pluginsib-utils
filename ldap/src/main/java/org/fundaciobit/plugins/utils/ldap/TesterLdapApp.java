package org.fundaciobit.plugins.utils.ldap;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 
 * @author anadal
 *
 */
public class TesterLdapApp extends JFrame implements LDAPConstants {

  public static final Map<String, String> trans = new HashMap<String, String>();

  public static final Map<String, String> defvalues = new HashMap<String, String>();

  static {
    trans.put("ldap.host_url", "URL");
    trans.put("ldap.security_authentication", "Tipus d'Autenticacio ('none' o 'simple') ");
    trans.put("ldap.security_principal", "Nom d'usuari");
    trans.put("ldap.security_credentials", "Contrasenya");
    trans.put("ldap.users_context_dn", "Context_DN");
    trans.put("ldap.search_scope", "Ambit de Cerca ('onelevel' o 'subtree'):");
    trans.put("ldap.search_filter", "Filtre de Cerca");
    trans.put("ldap.prefix_role_match_memberof", "Prefixe de Rols");
    trans.put("ldap.suffix_role_match_memberof", "Sufixe de Rols");
    trans.put("ldap.attribute.username", "Atribut[username]");
    trans.put("ldap.attribute.mail", "Atribut[Correu]");
    trans.put("ldap.attribute.administration_id", "Atribut[NIF]");
    trans.put("ldap.attribute.name", "Atribut[Nom]");
    trans.put(LDAP_SURNAMES_ATTRIBUTE, "Atribut[Llinatge1 o Llinatges]");
    trans.put(LDAP_SURNAME1_ATTRIBUTE, "Atribut[Llinatge1]");
    trans.put(LDAP_SURNAME2_ATTRIBUTE, "Atribut[Llinatge2]");
    trans.put("ldap.attribute.telephone", "Atribut[Telefon]");
    trans.put("ldap.attribute.memberof", "Atribut[Rols]");

    defvalues.put("ldap.host_url", "ldap://ldap.ibit.org:389");
    defvalues.put("ldap.security_authentication", "simple");

    defvalues.put("ldap.security_principal", "lectorldap");
    defvalues.put("ldap.security_credentials", "lectorldap");
    defvalues.put("ldap.users_context_dn", "cn=Users,dc=ibitnet,dc=lan");
    defvalues.put("ldap.search_scope", "onelevel");
    defvalues
        .put(
            "ldap.search_filter",
            "(|(memberOf=CN=@PFI_ADMIN,CN=Users,DC=ibitnet,DC=lan)(memberOf=CN=@PFI_USER,CN=Users,DC=ibitnet,DC=lan))");
    defvalues.put("ldap.prefix_role_match_memberof", "CN=@");
    defvalues.put("ldap.suffix_role_match_memberof", ",CN=Users,DC=ibitnet,DC=lan");
    defvalues.put("ldap.attribute.username", "sAMAccountName");
    defvalues.put("ldap.attribute.mail", "mail");
    defvalues.put("ldap.attribute.administration_id", "postOfficeBox");
    defvalues.put("ldap.attribute.name", "givenName");
    defvalues.put(LDAP_SURNAMES_ATTRIBUTE, "sn");
    defvalues.put(LDAP_SURNAME1_ATTRIBUTE, "sn1");
    defvalues.put(LDAP_SURNAME2_ATTRIBUTE, "sn2");
    defvalues.put("ldap.attribute.telephone", "telephoneNumber");
    defvalues.put("ldap.attribute.memberof", "memberOf");
  }

  public Map<String, JTextField> textFields = new HashMap<String, JTextField>();

  public JPanel wizardPanel = new JPanel(new CardLayout());

  public boolean showConfig = true;

  JButton bototest = new JButton("Provar");

  JTextArea results = new JTextArea();

  LDAPUserManager ldapUserManager;

  public void init() {

    JPanel cp = new JPanel();
    cp.setLayout(new GridBagLayout());

    cp.setBackground(UIManager.getColor("control"));
    GridBagConstraints c = new GridBagConstraints();

    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.insets = new Insets(2, 2, 2, 2);
    c.anchor = GridBagConstraints.EAST;

    for (String prop : LDAPConstants.LDAP_PROPERTIES) {
      cp.add(new JLabel(trans.get(prop) + ":", SwingConstants.RIGHT), c);
    }

    c.gridx = 1;
    c.gridy = GridBagConstraints.RELATIVE;
    c.weightx = 1.0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.CENTER;

    /*
     * c.gridx = 1; c.gridy = GridBagConstraints.RELATIVE;
     */

    for (String prop : LDAPConstants.LDAP_PROPERTIES) {
      JTextField tf = new JTextField(70);

      tf.setText(defvalues.get(prop));

      // cp.add(new JLabel(trans.get(prop) + ":", SwingConstants.RIGHT), c);

      cp.add(tf, c);

      textFields.put(prop, tf);
    }

    JPanel accions = new JPanel(new FlowLayout(FlowLayout.CENTER));

    accions.add(newJButton("authenticateUser", new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String username = JOptionPane.showInputDialog("Nom d'usuari:");
        if (username != null) {
          String password = JOptionPane.showInputDialog("Password:");
          if (password != null) {
            results.setText(authenticateUser(username, password));
          }
        }
      }
    }));

    accions.add(newJButton("getAllUserNames", new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        results.setText(getAllUserNames());
      }
    }));

    accions.add(newJButton("getUserArray", new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        results.setText(getUserArray());
      }
    }));

    accions.add(newJButton("getRolesOfUser", new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String username = JOptionPane.showInputDialog("Nom d'usuari:");
        if (username != null) {
          results.setText(getRolesOfUser(username));
        }
      }
    }));

    accions.add(newJButton("getUserByUsername", new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String username = JOptionPane.showInputDialog("Nom d'usuari:");
        if (username != null) {
          results.setText(getUserByUsername(username));
        }
      }
    }));

    accions.add(newJButton("getUserByAdministrationID", new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String nif = JOptionPane.showInputDialog("NIF:");
        if (nif != null) {
          results.setText(getUserByAdministrationID(nif));
        }
      }
    }));

    accions.add(newJButton("userExists", new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String username = JOptionPane.showInputDialog("Nom d'usuari:");
        if (username != null) {
          results.setText(userExists(username));
        }
      }
    }));

    // accions.add(new JButton("Guardar com"));

    JPanel testPanel = new JPanel(new BorderLayout());

    testPanel.add(new JScrollPane(results), BorderLayout.CENTER);
    testPanel.add(accions, BorderLayout.NORTH);

    wizardPanel.add("Step1", cp);
    wizardPanel.add("Step2", testPanel);

    bototest.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        CardLayout cl = (CardLayout) (wizardPanel.getLayout());
        if (showConfig == true) {

          cl.last(wizardPanel);
          showConfig = false;
          bototest.setText("Configuració");

          results.setText("");

          // Create Connection
          Properties ldapProperties = getPropertiesFromTextFields();
          ldapUserManager = new LDAPUserManager(ldapProperties);

        } else {
          cl.first(wizardPanel);
          showConfig = true;
          bototest.setText("Provar");
        }

      }
    });

    JPanel botons = new JPanel(new FlowLayout(FlowLayout.CENTER));

    botons.add(bototest);

    botons.add(newJButton("Carregar configuració", new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(".");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Properties",
            "properties");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(TesterLdapApp.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

          File f = chooser.getSelectedFile();

          Properties values = new Properties();
          try {
            values.load(new FileInputStream(f));

            for (String prop : LDAPConstants.LDAP_PROPERTIES) {
              JTextField tf = textFields.get(prop);
              tf.setText(values.getProperty(prop));
            }
          } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error obrint fitxer de propietats: " + e1.getMessage());
          }
        }
      }
    }));

    botons.add(newJButton("Guardar configuració", new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(".");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Properties",
            "properties");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(TesterLdapApp.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

          File f = chooser.getSelectedFile();

          Properties values = new Properties();
          try {

            for (String prop : LDAPConstants.LDAP_PROPERTIES) {
              JTextField tf = textFields.get(prop);
              values.setProperty(prop, tf.getText());
            }

            values.store(new FileOutputStream(f), "Ldap Props");
          } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error obrint fitxer de propietats: " + e1.getMessage());
          }
        }

      }
    }));

    this.setTitle("LDAP tester");
    Container cont = this.getContentPane();
    cont.setLayout(new BorderLayout());

    cont.add(wizardPanel, BorderLayout.CENTER);

    cont.add(botons, BorderLayout.SOUTH);

  }

  protected JButton newJButton(String name, ActionListener action) {

    JButton b = new JButton(name);
    b.addActionListener(action);
    return b;

  }

  protected String authenticateUser(String username, String password) {
    try {
      boolean res = this.ldapUserManager.authenticateUser(username, password);
      return "authenticateUser() => " + res;
    } catch (Exception e) {
      return exceptionToString(e);
    }

  }

  protected String getAllUserNames() {
    try {
      List<String> usernames = this.ldapUserManager.getAllUserNames();
      StringBuffer str = new StringBuffer(" ALL USERNAMES (" + usernames.size() + ")\n");
      int i = 0;
      for (String un : usernames) {
        str.append((i++) + ".- " + un + "\n");
      }

      return str.toString();
    } catch (Exception e) {
      return exceptionToString(e);
    }
  }

  protected String getRolesOfUser(String username) {
    try {
      List<String> roles = this.ldapUserManager.getRolesOfUser(username);
      StringBuffer str = new StringBuffer(" ALL ROLES OF USERNAME " + username);

      if (roles == null) {
        str.append(" = NULL !!!!!");
      } else {
        str.append(" ( LEN = " + roles.size() + ")\n");

        for (String rol : roles) {
          str.append("    - " + rol + "\n");
        }
      }

      return str.toString();
    } catch (Exception e) {
      return exceptionToString(e);
    }
  }

  protected String getUserArray() {
    try {
      LDAPUser[] users = this.ldapUserManager.getUserArray();

      StringBuffer str = new StringBuffer("ALL USER INFO ");

      if (users == null) {
        str.append(" NULL !!!!!");
      } else {
        str.append(" ( LEN = " + users.length + ")\n");
        for (LDAPUser ldapUser : users) {
          str.append("-----------------\n");
          setLDAPUserToStringBuffer(ldapUser, str);
        }

      }

      return str.toString();
    } catch (Exception e) {
      return exceptionToString(e);
    }
  }

  protected String getUserByAdministrationID(String nif) {
    try {
      LDAPUser user = this.ldapUserManager.getUserByAdministrationID(nif);
      StringBuffer str = new StringBuffer("USERNAME BY NIF " + nif);

      if (user == null) {
        str.append(" NULL !!!!!");
      } else {
        setLDAPUserToStringBuffer(user, str);

      }

      return str.toString();
    } catch (Exception e) {
      return exceptionToString(e);
    }
  }

  protected String getUserByUsername(String username) {
    try {
      LDAPUser user = this.ldapUserManager.getUserByUsername(username);
      StringBuffer str = new StringBuffer("USERNAME " + username
          + "\n=================================\n");

      if (user == null) {
        str.append(" NULL !!!!!");
      } else {
        setLDAPUserToStringBuffer(user, str);

      }

      return str.toString();
    } catch (Exception e) {
      return exceptionToString(e);
    }
  }

  protected String userExists(String username) {
    try {

      StringBuffer str = new StringBuffer("USERNAME EXISTS " + username + "? "
          + this.ldapUserManager.userExists(username));

      return str.toString();
    } catch (Exception e) {
      return exceptionToString(e);
    }
  }

  protected void setLDAPUserToStringBuffer(LDAPUser user, StringBuffer str) {
    str.append("getUserName: " + user.getUserName() + " \n");

    str.append("getAdministrationID: " + user.getAdministrationID() + " \n");
    str.append("getName: " + user.getName() + " \n");
    str.append("getSurname: " + LDAPUser.getCorrectSurname(user) + " \n");
    str.append("getEmail: " + user.getEmail() + " \n");
    str.append("getTelephoneNumber: " + user.getTelephoneNumber() + " \n");
  }

  protected Properties getPropertiesFromTextFields() {
    Properties ldapProperties = new Properties();
    for (String prop : LDAPConstants.LDAP_PROPERTIES) {
      JTextField tf = textFields.get(prop);
      ldapProperties.setProperty(prop, tf.getText());
    }
    return ldapProperties;
  }

  public String exceptionToString(Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    return sw.toString(); // stack trace as a string
  }

  public static void main(String[] args) {

    TesterLdapApp testApp = new TesterLdapApp();

    testApp.init();

    testApp.pack();
    testApp.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        System.exit(0);
      }
    });
    testApp.setVisible(true);

  }

}
