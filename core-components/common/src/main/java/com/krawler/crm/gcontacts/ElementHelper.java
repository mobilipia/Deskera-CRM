/*
 * Copyright (C) 2012  Krawler Information Systems Pvt Ltd
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package com.krawler.crm.gcontacts;

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.Nickname;
import com.google.gdata.data.contacts.ShortName;
import com.google.gdata.data.contacts.Website;
import com.google.gdata.data.extensions.AdditionalName;
import com.google.gdata.data.extensions.City;
import com.google.gdata.data.extensions.Country;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.FamilyName;
import com.google.gdata.data.extensions.FormattedAddress;
import com.google.gdata.data.extensions.FullName;
import com.google.gdata.data.extensions.GivenName;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.data.extensions.NamePrefix;
import com.google.gdata.data.extensions.NameSuffix;
import com.google.gdata.data.extensions.Neighborhood;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.data.extensions.PoBox;
import com.google.gdata.data.extensions.PostCode;
import com.google.gdata.data.extensions.Region;
import com.google.gdata.data.extensions.Street;
import com.google.gdata.data.extensions.StructuredPostalAddress;

import java.io.PrintStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public enum ElementHelper implements ElementHelperInterface {



  EMAIL(true) {
    public void parse(ContactEntry contact, ElementParser parser) {
      Email email = new Email();
      email.setAddress(parser.get(PropertyName.VALUE));
      if (parser.has(PropertyName.REL)) {
        email.setRel(parser.get(PropertyName.REL));
      }
      if (parser.has(PropertyName.LABEL)) {
        email.setLabel(parser.get(PropertyName.LABEL));
      }
      if (parser.has(PropertyName.PRIMARY)) {
        email.setPrimary(parser.is(PropertyName.PRIMARY));
      }
      contact.addEmailAddress(email);
    }

    public void print(PrintStream out, ContactEntry contact) {
      if (contact.hasEmailAddresses()) {
        out.println("{ email addresses:[");
        for (Email email : contact.getEmailAddresses()) {
          out.print(email.getAddress()+",");
        }
        out.println("]},");
      }
    }

    public void update(ContactEntry dest, ContactEntry src) {
      if (src.hasEmailAddresses()) {
        List<Email> emailAddresses = dest.getEmailAddresses();
        emailAddresses.clear();
        emailAddresses.addAll(src.getEmailAddresses());
      }
    }

    public String getUsage() {
      return "<email>"
          + "[,rel:<rel>]"
          + "[,label:<label>]"
          + "[,primary:true|false]";
    }
  },


  NAME {
    public void parse(ContactEntry contact, ElementParser parser) {
      Name name = new Name();
      name.setFullName(new FullName(parser.get(PropertyName.VALUE), null));
      if (parser.has(PropertyName.GIVEN)) {
        name.setGivenName(new GivenName(parser.get(PropertyName.GIVEN), null));
      }
      if (parser.has(PropertyName.FAMILY)) {
        name.setFamilyName(
            new FamilyName(parser.get(PropertyName.FAMILY), null));
      }
      if (parser.has(PropertyName.ADDITIONAL)) {
        name.setAdditionalName(
            new AdditionalName(parser.get(PropertyName.ADDITIONAL), null));
      }
      if (parser.has(PropertyName.PREFIX)) {
        name.setNamePrefix(new NamePrefix(parser.get(PropertyName.PREFIX)));
      }
      if (parser.has(PropertyName.SUFFIX)) {
        name.setNameSuffix(new NameSuffix(parser.get(PropertyName.SUFFIX)));
      }
      contact.setName(name);
    }

    public void parseGroup(ContactGroupEntry group, ElementParser parser) {
      group.setTitle(new PlainTextConstruct(parser.get(PropertyName.VALUE)));
    }

    public void print(PrintStream out, ContactEntry contact) {
//      if (contact.hasName()) {
//        out.println("structured name: ");
//        Name name = contact.getName();
//        if (name.hasFullName()) {
//          out.print(" full name: " + name.getFullName().getValue());
//        }
//        if (name.hasGivenName()) {
//          out.print(" given name: " + name.getGivenName().getValue());
//        }
//        if (name.hasFamilyName()) {
//          out.print(" family name: " + name.getFamilyName().getValue());
//        }
//        if (name.hasAdditionalName()) {
//          out.print(" additional name: " + name.getAdditionalName().getValue());
//        }
//        if (name.hasNamePrefix()) {
//          out.print(" prefix: " + name.getNamePrefix().getValue());
//        }
//        if (name.hasNameSuffix()) {
//          out.print(" suffix: " + name.getNameSuffix().getValue());
//        }
//        out.println();
//      }
    }

    public void update(ContactEntry dest, ContactEntry src) {
      if (src.hasName()) {
        dest.setName(src.getName());
      }
    }

    public String getUsage() {
      return "<name>"
          + "[,given:<givenName]"
          + "[,family:<familyName>]"
          + "[,additional:additionalName]"
          + "[,prefix:<prefix>]"
          + "[,suffix:<suffix>]";
    }
  },

  PHONE(true) {
    public void parse(ContactEntry contact, ElementParser parser) {
      PhoneNumber phone = new PhoneNumber();
      phone.setPhoneNumber(parser.get(PropertyName.VALUE));
      if (parser.has(PropertyName.REL)) {
        phone.setRel(parser.get(PropertyName.REL));
      }
      if (parser.has(PropertyName.LABEL)) {
        phone.setLabel(parser.get(PropertyName.LABEL));
      }
      if (parser.has(PropertyName.URI)) {
        phone.setUri(parser.get(PropertyName.URI));
      }
      if (parser.has(PropertyName.PRIMARY)) {
        phone.setPrimary(parser.is(PropertyName.PRIMARY));
      }
      contact.addPhoneNumber(phone);
    }

    public void print(PrintStream out, ContactEntry contact) {
      if (contact.hasPhoneNumbers()) {
        out.println("{ phone numbers:");
        out.print(" [");
        for (PhoneNumber phone : contact.getPhoneNumbers()) {

            out.print(" \" " + phone.getPhoneNumber()+"\",");
        }
        out.print(" ]}, ");
      }
    }

    public void update(ContactEntry dest, ContactEntry src) {
      if (src.hasPhoneNumbers()) {
        List<PhoneNumber> phoneNumbers = dest.getPhoneNumbers();
        phoneNumbers.clear();
        phoneNumbers.addAll(src.getPhoneNumbers());
      }
    }

    public String getUsage() {
      return "<phone>"
          + "[,rel:<rel>]"
          + "[,label:<label>]"
          + "[,uri:<uri>]"
          + "[,primary:true|false]";
    }
  },
    POSTAL(true) {
    public void parse(ContactEntry contact, ElementParser parser) {
      StructuredPostalAddress address = new StructuredPostalAddress();
      if (parser.has(PropertyName.REL)) {
        address.setRel(parser.get(PropertyName.REL));
      }
      if (parser.has(PropertyName.LABEL)) {
        address.setLabel(parser.get(PropertyName.LABEL));
      }
      if (parser.has(PropertyName.PRIMARY)) {
        address.setPrimary(parser.is(PropertyName.PRIMARY));
      }
      if (parser.has(PropertyName.CITY)) {
        address.setCity(new City(parser.get(PropertyName.CITY)));
      }
      if (parser.has(PropertyName.COUNTRY)) {
        // Don't care about country code
        address.setCountry(new Country(null, parser.get(PropertyName.COUNTRY)));
      }
      if (parser.has(PropertyName.FORMATTED)) {
        address.setFormattedAddress(
            new FormattedAddress(parser.get(PropertyName.FORMATTED)));
      }
      if (parser.has(PropertyName.NEIGHBORHOOD)) {
        address.setNeighborhood(
            new Neighborhood(parser.get(PropertyName.NEIGHBORHOOD)));
      }
      if (parser.has(PropertyName.POBOX)) {
        address.setPobox(new PoBox(parser.get(PropertyName.POBOX)));
      }
      if (parser.has(PropertyName.POSTCODE)) {
        address.setPostcode(new PostCode(parser.get(PropertyName.POSTCODE)));
      }
      if (parser.has(PropertyName.REGION)) {
        address.setRegion(new Region(parser.get(PropertyName.REGION)));
      }
      if (parser.has(PropertyName.STREET)) {
        address.setStreet(new Street(parser.get(PropertyName.STREET)));
      }
      contact.addStructuredPostalAddress(address);
    }

    public void print(PrintStream out, ContactEntry contact) {
      if (contact.hasStructuredPostalAddresses()) {
        int i =0 ;
        out.println("{ addresses:[ ");
        for (StructuredPostalAddress address
            : contact.getStructuredPostalAddresses()) {
          out.print("  ");
          if (address.hasLabel()) {
            out.print(" label:" + address.getLabel());
          }
          if (address.hasCity()) {
            out.print(" city:" + address.getCity().getValue());
          }
          if (address.hasCountry()) {
            out.print(" country:" + address.getCountry().getValue());
          }
          if (address.hasFormattedAddress()) {
            i++;
            out.print(i+". " + address.getFormattedAddress().getValue()+",");
          }
          if (address.hasNeighborhood()) {
            out.print(" neighborhood:" + address.getNeighborhood().getValue());
          }
          if (address.hasPobox()) {
            out.print(" pobox:" + address.getPobox().getValue());
          }
          if (address.hasPostcode()) {
            out.print(" postcode:" + address.getPostcode().getValue());
          }
          if (address.hasRegion()) {
            out.print(" region:" + address.getRegion().getValue());
          }
          if (address.hasStreet()) {
            out.print(" street:" + address.getStreet().getValue());
          }
          if (address.getPrimary()) {
            out.print(" (primary)");
          }
          out.println();
        }
        out.println("]}, ");
      }
    }

    public void update(ContactEntry dest, ContactEntry src) {
      if (src.hasStructuredPostalAddresses()) {
        List<StructuredPostalAddress> structuredPostalAddresses =
            dest.getStructuredPostalAddresses();
        structuredPostalAddresses.clear();
        structuredPostalAddresses.addAll(src.getStructuredPostalAddresses());
      }
    }

    public String getUsage() {
      return "[rel:<rel>]"
          + "[,label:<label>]"
          + "[,primary:true|false]"
          + "[,city:<city>]"
          + "[,country:<country>]"
          + "[,formatted:<formattedAddress>]"
          + "[,neighborhood:<neighborhood>]"
          + "[,pobox:<poBox>]"
          + "[,postcode:<postCode>]"
          + "[,region:<region>]"
          + "[,street:<street>]";
    }
  },
//  NICKNAME {
//    public void parse(ContactEntry contact, ElementParser parser) {
//      Nickname nickname = new Nickname();
//      nickname.setValue(parser.get(PropertyName.VALUE));
//      contact.setNickname(nickname);
//    }
//
//    public void print(PrintStream out, ContactEntry contact) {
//      if (contact.hasNickname()) {
//        out.println("nickname: " + contact.getNickname().getValue());
//      }
//    }
//
//    public void update(ContactEntry dest, ContactEntry src) {
//      if (src.hasNickname()) {
//        dest.setNickname(src.getNickname());
//      }
//    }
//
//    public String getUsage() {
//      return "<nickname>";
//    }
//  },


//  SHORT_NAME {
//    public void parse(ContactEntry contact, ElementParser parser) {
//      ShortName shortName = new ShortName();
//      shortName.setValue(parser.get(PropertyName.VALUE));
//      contact.setShortName(shortName);
//    }
//
//    public void print(PrintStream out, ContactEntry contact) {
//      if (contact.hasShortName()) {
//        out.println("short name:" + contact.getShortName().getValue());
//      }
//    }
//
//    public void update(ContactEntry dest, ContactEntry src) {
//      if (src.hasShortName()) {
//        dest.setShortName(src.getShortName());
//      }
//    }
//
//    public String getUsage() {
//      return "<short_name>";
//    }
//  },
//
//  POSTAL(true) {
//    public void parse(ContactEntry contact, ElementParser parser) {
//      StructuredPostalAddress address = new StructuredPostalAddress();
//      if (parser.has(PropertyName.REL)) {
//        address.setRel(parser.get(PropertyName.REL));
//      }
//      if (parser.has(PropertyName.LABEL)) {
//        address.setLabel(parser.get(PropertyName.LABEL));
//      }
//      if (parser.has(PropertyName.PRIMARY)) {
//        address.setPrimary(parser.is(PropertyName.PRIMARY));
//      }
//      if (parser.has(PropertyName.CITY)) {
//        address.setCity(new City(parser.get(PropertyName.CITY)));
//      }
//      if (parser.has(PropertyName.COUNTRY)) {
//        // Don't care about country code
//        address.setCountry(new Country(null, parser.get(PropertyName.COUNTRY)));
//      }
//      if (parser.has(PropertyName.FORMATTED)) {
//        address.setFormattedAddress(
//            new FormattedAddress(parser.get(PropertyName.FORMATTED)));
//      }
//      if (parser.has(PropertyName.NEIGHBORHOOD)) {
//        address.setNeighborhood(
//            new Neighborhood(parser.get(PropertyName.NEIGHBORHOOD)));
//      }
//      if (parser.has(PropertyName.POBOX)) {
//        address.setPobox(new PoBox(parser.get(PropertyName.POBOX)));
//      }
//      if (parser.has(PropertyName.POSTCODE)) {
//        address.setPostcode(new PostCode(parser.get(PropertyName.POSTCODE)));
//      }
//      if (parser.has(PropertyName.REGION)) {
//        address.setRegion(new Region(parser.get(PropertyName.REGION)));
//      }
//      if (parser.has(PropertyName.STREET)) {
//        address.setStreet(new Street(parser.get(PropertyName.STREET)));
//      }
//      contact.addStructuredPostalAddress(address);
//    }
//
//    public void print(PrintStream out, ContactEntry contact) {
//      if (contact.hasStructuredPostalAddresses()) {
//        out.println("addresses:");
//        for (StructuredPostalAddress address
//            : contact.getStructuredPostalAddresses()) {
//          out.print("  ");
//          if (address.hasRel()) {
//            out.print(" rel:" + address.getRel());
//          }
//          if (address.hasLabel()) {
//            out.print(" label:" + address.getLabel());
//          }
//          if (address.hasCity()) {
//            out.print(" city:" + address.getCity().getValue());
//          }
//          if (address.hasCountry()) {
//            out.print(" country:" + address.getCountry().getValue());
//          }
//          if (address.hasFormattedAddress()) {
//            out.print(" formatted:" + address.getFormattedAddress().getValue());
//          }
//          if (address.hasNeighborhood()) {
//            out.print(" neighborhood:" + address.getNeighborhood().getValue());
//          }
//          if (address.hasPobox()) {
//            out.print(" pobox:" + address.getPobox().getValue());
//          }
//          if (address.hasPostcode()) {
//            out.print(" postcode:" + address.getPostcode().getValue());
//          }
//          if (address.hasRegion()) {
//            out.print(" region:" + address.getRegion().getValue());
//          }
//          if (address.hasStreet()) {
//            out.print(" street:" + address.getStreet().getValue());
//          }
//          if (address.getPrimary()) {
//            out.print(" (primary)");
//          }
//          out.println();
//        }
//      }
//    }
//
//    public void update(ContactEntry dest, ContactEntry src) {
//      if (src.hasStructuredPostalAddresses()) {
//        List<StructuredPostalAddress> structuredPostalAddresses =
//            dest.getStructuredPostalAddresses();
//        structuredPostalAddresses.clear();
//        structuredPostalAddresses.addAll(src.getStructuredPostalAddresses());
//      }
//    }
//
//    public String getUsage() {
//      return "[rel:<rel>]"
//          + "[,label:<label>]"
//          + "[,primary:true|false]"
//          + "[,city:<city>]"
//          + "[,country:<country>]"
//          + "[,formatted:<formattedAddress>]"
//          + "[,neighborhood:<neighborhood>]"
//          + "[,pobox:<poBox>]"
//          + "[,postcode:<postCode>]"
//          + "[,region:<region>]"
//          + "[,street:<street>]";
//    }
//  },


  WEBSITE(true) {
    public void parse(ContactEntry contact, ElementParser parser) {
      Website website = new Website();
      website.setHref(parser.get(PropertyName.VALUE));
      if (parser.has(PropertyName.REL)) {
        website.setRel(
            Website.Rel.valueOf(parser.get(PropertyName.REL).toLowerCase()));
      }
      if (parser.has(PropertyName.LABEL)) {
        website.setLabel(parser.get(PropertyName.LABEL));
      }
      if (parser.has(PropertyName.PRIMARY)) {
        website.setPrimary(parser.is(PropertyName.PRIMARY));
      }
      contact.addWebsite(website);
    }

    public void print(PrintStream out, ContactEntry contact) {
//      if (contact.hasWebsites()) {
//        out.println("websites:");
//        for (Website website : contact.getWebsites()) {
//          out.print("  " + website.getHref());
//          if (website.hasRel()) {
//            out.print(" ref:" + website.getRel().toString().toLowerCase());
//          }
//          if (website.hasLabel()) {
//            out.print(" label:" + website.getLabel());
//          }
//          if (website.getPrimary()) {
//            out.print(" (primary)");
//          }
//          out.println();
//        }
//      }
    }

    public void update(ContactEntry dest, ContactEntry src) {
      if (src.hasWebsites()) {
        List<Website> websites = dest.getWebsites();
        websites.clear();
        websites.addAll(src.getWebsites());
      }
    }

    public String getUsage() {
      return "<url>"
          + "[,rel:<rel>]"
          + "[label:<label>]"
          + "[,primary:true|false]";
    }
  };

  // Flag to indicate if the element can be repeated.
  private final boolean repetable;

  // some regexp for parameter parsing/checking
  private final static Pattern REPEATED_ARG_PATTERN
      = Pattern.compile("^(\\D+)\\d*$");

  // Constructors.
  private ElementHelper(boolean repetable) { this.repetable = repetable; }
  private ElementHelper() { this(false); }

  /**
   * The default implementation just throws an UnsuportedOperationException, and
   * only those helpers override it, what are used in parsing groups elements.
   *
   * @param group  the group the parsed element should be added or set.
   * @param parser the parser used for the parsing of the description.
   *
   * @throws UnsupportedOperationException in case the specific element can not
   *         be set on a ContactGroupEntry.
   *
   * @see ElementParser
   */
  public void parseGroup(ContactGroupEntry group, ElementParser parser) {
    throw new UnsupportedOperationException("parseGroup not supported for"
        + this.toString().toLowerCase() + " element");
  }

  private static ElementHelper find(String name)
      throws IllegalArgumentException {
    Matcher m = REPEATED_ARG_PATTERN.matcher(name);
    if (!m.matches()) {
      throw new IllegalArgumentException("badly formated parameter: " + name);
    }
    try {
      return valueOf(m.group(1).toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("unknown parameter: " + name);
    }
  }

  /**
   * Builds a contact from the list of element descriptions.
   * It delegates the element specific parsing to the appropriate helper
   * instances. The actual element type is matched by the name of the enum
   * instance, so the element names specified in the parameters should (almost)
   * match the name of enum instances. The exceptions are those elements what
   * can be repeated, when the parameter format is "name<n>".* Due to this
   * formating convention we cannot use directly the valueOf() facility of the
   * enum.
   *
   * @param contact    the contact to build.
   * @param parameters list of element descriptions.
   *
   */
  public static void buildContact(ContactEntry contact,
      List<String> parameters) {
    for (String string : parameters) {
      if (!string.startsWith("--")) {
        throw new IllegalArgumentException("unknown argument: " + string);
      }
      String param = string.substring(2);
      String params[] = param.split("=", 2);
      if (params.length != 2) {
        throw new IllegalArgumentException("badly formated argument: "
            + string);
      }
      ElementHelper helper = find(params[0]);
      if (helper == null) {
        throw new IllegalArgumentException("unknown argument: " + string);
      }
      helper.parse(contact, new ElementParser(params[1]));
    }
  }

  /**
   * Builds a group from the list of element descriptions.
   * It delegates the element specific parsing to the appropriate helper
   * instances. The actual element type is matched by the name of the enum
   * instance, so the element names specified in the parameters should (almost)
   * match the name of enum instances. The exceptions are those elements what
   * can be repeated, when the parameter format is "name<n>".* Due to this
   * formating convention we cannot use directly the valueOf() facility of the
   * enum.
   *
   * @param group      the group to build.
   * @param parameters list of element descriptions.
   *
   */
  public static void buildGroup(ContactGroupEntry group,
      List<String> parameters) {
    for (String string : parameters) {
      if (!string.startsWith("--")) {
        throw new IllegalArgumentException("unknown argument: " + string);
      }
      String param = string.substring(2);
      String params[] = param.split("=", 2);
      if (params.length != 2) {
        throw new IllegalArgumentException("badly formated argument: "
            + string);
      }
      ElementHelper helper = find(params[0]);
      if (helper == null) {
        throw new IllegalArgumentException("unknown argument: " + string);
      }
      helper.parseGroup(group, new ElementParser(params[1]));
    }
  }

  /**
   * Updates the elements of a contact entry based on the elements of another
   * contact entry.
   * Those elements are replaced in the destination contact entry what are
   * exists in the source contact. Those elements not contained by the source
   * contact are left unchanged on the destination contact.
   *
   * @param dest the destination contact to be updated.
   * @param src  the source contact
   */
  public static void updateContact(ContactEntry dest, ContactEntry src) {
    for (ElementHelper helper : values()) {
      helper.update(dest, src);
    }
  }

  /**
   * Prints the content of the contact in a human readable form.
   *
   * @param out     the stream to print to.
   * @param contact the contact to be printed out.
   */
  public static void printContact(PrintStream out, ContactEntry contact) {
    for (ElementHelper helper : values()) {
      helper.print(out, contact);
    }
  }

  /**
   * Gives the usage help text of all elements.
   *
   * @return the usage help text for all elements.
   */
  public static String getUsageString() {
    StringBuffer buffer = new StringBuffer();
    for (ElementHelper helper : values()) {
      buffer.append("             --" + helper.toString().toLowerCase());
      if (helper.repetable) {
        buffer.append("<n>");
      }
      buffer.append("=" + helper.getUsage() + "\n");
    }
    buffer.append(
        "Notes! <n> is a unique number for the field - several fields\n"
        + " of the same type can be present (example: im1, im2, im3).\n"
        + " Available rels and protocols can be looked up in the \n"
        + " feed documentation.\n");
    return buffer.toString();
  }
}
