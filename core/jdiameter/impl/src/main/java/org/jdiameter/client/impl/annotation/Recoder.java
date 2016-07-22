 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.client.impl.annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.MetaData;
import org.jdiameter.api.Request;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.annotation.AvpDscr;
import org.jdiameter.api.annotation.AvpFlag;
import org.jdiameter.api.annotation.AvpType;
import org.jdiameter.api.annotation.Child;
import org.jdiameter.api.annotation.CommandDscr;
import org.jdiameter.api.annotation.CommandFlag;
import org.jdiameter.api.annotation.Getter;
import org.jdiameter.api.annotation.Setter;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.annotation.IRecoder;
import org.jdiameter.client.api.annotation.RecoderException;
import org.jdiameter.client.impl.RawSessionImpl;
import org.jdiameter.client.impl.annotation.internal.ClassInfo;
import org.jdiameter.client.impl.annotation.internal.ConstructorInfo;
import org.jdiameter.client.impl.annotation.internal.MethodInfo;
import org.jdiameter.client.impl.annotation.internal.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class Recoder implements IRecoder {

  // TODO full min/max/position constrains and optimization (caching)

  private static final Logger log = LoggerFactory.getLogger(Recoder.class);

  private Storage storage = new Storage();
  private final RawSessionImpl rawSession;
  private final MetaData metaData;

  public Recoder(SessionFactory factory, MetaData metaData) {
    this.metaData = metaData;
    try {
      this.rawSession = (RawSessionImpl) factory.getNewRawSession();
    } catch (InternalException e) {
      throw new IllegalArgumentException(e);
    }
  }
  // =======================================================================================


  //@Override
  @Override
  public Message encodeToRequest(Object yourDomainMessageObject, Avp... additionalAvp) throws RecoderException {
    return encode(yourDomainMessageObject, null, 0, additionalAvp);
  }

  //@Override
  @Override
  public Message encodeToAnswer(Object yourDomainMessageObject, Request request, long resultCode) throws RecoderException {
    return encode(yourDomainMessageObject, request,  resultCode);
  }

  public Message encode(Object yourDomainMessageObject, Request request, long resultCode, Avp... addAvp) throws RecoderException {
    IMessage message = null;
    ClassInfo classInfo = storage.getClassInfo(yourDomainMessageObject.getClass());
    CommandDscr commandDscr = classInfo.getAnnotation(CommandDscr.class);
    if (commandDscr != null) {
      // Get command parameters
      if (request == null) {
        message = (IMessage) rawSession.createMessage(commandDscr.code(), ApplicationId.createByAccAppId(0));
        message.setRequest(true);
        message.getAvps().addAvp(addAvp);
        try {
          if (message.getAvps().getAvp(Avp.AUTH_APPLICATION_ID) != null) {
            message.setHeaderApplicationId(message.getAvps().getAvp(Avp.AUTH_APPLICATION_ID).getUnsigned32());
          }
          else if (message.getAvps().getAvp(Avp.ACCT_APPLICATION_ID) != null) {
            message.setHeaderApplicationId(message.getAvps().getAvp(Avp.ACCT_APPLICATION_ID).getUnsigned32());
          }
          else if (message.getAvps().getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID) != null) {
            message.setHeaderApplicationId(message.getAvps().getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID).
                getGrouped().getAvp(Avp.VENDOR_ID).getUnsigned32());
          }
        } catch (Exception exc) {
          throw new RecoderException(exc);
        }
        if (message.getAvps().getAvp(Avp.ORIGIN_HOST) == null) {
          message.getAvps().addAvp(Avp.ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
        }
        if (message.getAvps().getAvp(Avp.ORIGIN_REALM) == null) {
          message.getAvps().addAvp(Avp.ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
        }
      } else {
        message = (IMessage) request.createAnswer(resultCode);
      }
      for (CommandFlag f : commandDscr.flags()) {
        switch (f) {
          case E:
            message.setError(true);
            break;
          case P:
            message.setProxiable(true);
            break;
          case R:
            message.setRequest(true);
            break;
          case T:
            message.setReTransmitted(true);
            break;
        }
      }
      // Find top level avp in getter-annotation methods
      Map<String, Object> chMap = getChildInstance(yourDomainMessageObject, classInfo, null);
      // Fill
      for (Child ch : commandDscr.childs()) {
        fillChild(message.getAvps(), ch, chMap);
      }
    } else {
      log.debug("Can not found annotation for object {}", yourDomainMessageObject);
    }
    return message;
  }

  private Map<String, Object> getChildInstance(Object yourDomainMessageObject, ClassInfo c, Map<String, Object> chMap)
      throws RecoderException {
    if (chMap == null) {
      chMap = new HashMap<String, Object>();
    }
    for (MethodInfo mi : c.getMethodsInfo()) {
      if (mi.getAnnotation(Getter.class) != null) {
        try {
          Object value = mi.getMethod().invoke(yourDomainMessageObject);
          if (value != null) {
            Class mc = value.getClass().isArray() ? value.getClass().getComponentType() : value.getClass();
            chMap.put(mc.getName(), value);
            for (Class<?> i : mc.getInterfaces()) {
              chMap.put(i.getName(), value);
            }
          }
        } catch (IllegalAccessException e) {
          throw new RecoderException(e);
        } catch (InvocationTargetException e) {
          throw new RecoderException(e);
        }
      }
    }
    return chMap;
  }

  private void fillChild(AvpSet as, Child ci, Map<String, Object> childs) throws RecoderException {
    Object c = childs.get(ci.ref().getName());
    if (c != null) {
      ClassInfo cc = storage.getClassInfo(ci.ref());
      AvpDscr ad = cc.getAnnotation(AvpDscr.class);
      if (ad != null) {
        boolean m = false, p = false;
        // cast <=> getter for primitive
        switch (ad.type()) {
          case Integer32:
          case Enumerated: {
            for (AvpFlag f : ad.must()) {
              if (AvpFlag.M.equals(f)) {
                m = true;
              }
              else if (AvpFlag.P.equals(f)) {
                p = true;
              }
            }
            // find in getter
            Collection<Integer> cv = getValue(c, Integer.class);
            for (Integer v : cv) {
              as.addAvp(ad.code(), v, ad.vendorId(), m, p);
            }
          }
            break;
          case Unsigned32: {
            for (AvpFlag f : ad.must()) {
              if (AvpFlag.M.equals(f)) {
                m = true;
              }
              else if (AvpFlag.P.equals(f)) {
                p = true;
              }
            }
            Collection<Long> cv = getValue(c, Long.class);
            for (Long v : cv) {
              as.addAvp(ad.code(), v, ad.vendorId(), m, p, true);
            }
          }
          break;
          case Unsigned64:
          case Integer64: {
            for (AvpFlag f : ad.must()) {
              if (AvpFlag.M.equals(f)) {
                m = true;
              }
              else if (AvpFlag.P.equals(f)) {
                p = true;
              }
            }
            Collection<Long> cv = getValue(c, Long.class);
            for (Long v : cv) {
              as.addAvp(ad.code(), v, ad.vendorId(), m, p);
            }
          }
            break;
          case Float32: {
            for (AvpFlag f : ad.must()) {
              if (AvpFlag.M.equals(f)) {
                m = true;
              }
              else if (AvpFlag.P.equals(f)) {
                p = true;
              }
            }
            Collection<Float> cv = getValue(c, Float.class);
            for (Float v : cv) {
              as.addAvp(ad.code(), v, ad.vendorId(), m, p);
            }
          }
          break;
          case Float64: {
            for (AvpFlag f : ad.must()) {
              if (AvpFlag.M.equals(f)) {
                m = true;
              }
              else if (AvpFlag.P.equals(f)) {
                p = true;
              }
            }
            Collection<Double> cv = getValue(c, Double.class);
            for (Double v : cv) {
              as.addAvp(ad.code(), v, ad.vendorId(), m, p);
            }
          }
          break;
          case OctetString:
          case Address:
          case Time:
          case DiameterIdentity:
          case DiameterURI:
          case IPFilterRule:
          case QoSFilterRule: {
            for (AvpFlag f : ad.must()) {
              if (AvpFlag.M.equals(f)) {
                m = true;
              }
              else if (AvpFlag.P.equals(f)) {
                p = true;
              }
            }
            Collection<String> cv = getValue(c, String.class);
            for (String v : cv) {
              as.addAvp(ad.code(), v, ad.vendorId(), m, p, true);
            }
          }
            break;
          case UTF8String: {
            for (AvpFlag f : ad.must()) {
              if (AvpFlag.M.equals(f)) {
                m = true;
              }
              else if (AvpFlag.P.equals(f)) {
                p = true;
              }
            }
            Collection<String> cv = getValue(c, String.class);
            for (String v : cv) {
              as.addAvp(ad.code(), v, ad.vendorId(), m, p, false);
            }
          }
          break;
          case Grouped: {
            for (AvpFlag f : ad.must()) {
              if (AvpFlag.M.equals(f)) {
                m = true;
              }
              else if (AvpFlag.P.equals(f)) {
                p = true;
              }
            }
            Collection<Object> cv = new ArrayList<Object>();
            if (c.getClass().isArray()) {
              cv = Arrays.asList((Object[]) c);
            }
            else {
              cv.add(c);
            }
            for (Object cj : cv) {
              AvpSet las = as.addGroupedAvp(ad.code(), ad.vendorId(), m, p);
              Map<String, Object> lchilds = getChildInstance(cj, storage.getClassInfo(cj.getClass()), null);
              for (Child lci : ad.childs()) {
                fillChild(las, lci, lchilds);
              }
            }
          }
          break;
        }
      }
    }
  }

  private <T> Collection<T> getValue(Object ic, Class<T> type) throws RecoderException {
    Collection<T> rc = new ArrayList<T>();
    Object[] xc = null;
    if (ic.getClass().isArray()) {
      xc = (Object[]) ic;
    }
    else {
      xc = new Object[] {ic};
    }
    for (Object  c : xc) {
      for (MethodInfo lm : storage.getClassInfo(c.getClass()).getMethodsInfo()) {
        if (lm.getAnnotation(Getter.class) != null) {
          try {
            rc.add((T) lm.getMethod().invoke(c));
          } catch (IllegalAccessException e) {
            throw new RecoderException(e);
          } catch (InvocationTargetException e) {
            throw new RecoderException(e);
          }
        }
      }
    }
    return rc;
  }

  // =======================================================================================

  @Override
  public <T> T decode(Message message, java.lang.Class<T> yourDomainMessageObject) throws RecoderException {
    Object rc = null;
    ClassInfo c = storage.getClassInfo(yourDomainMessageObject);
    CommandDscr cd = c.getAnnotation(CommandDscr.class);
    if (cd != null) {
      try {
        if (message.getCommandCode() != cd.code()) {
          throw new IllegalArgumentException("Invalid message code " + message.getCommandCode());
        }
        if (message.getApplicationId() != 0 && message.getApplicationId() != cd.appId()) {
          throw new IllegalArgumentException("Invalid Application-Id " + message.getApplicationId());
        }
        for (CommandFlag f : cd.flags()) {
          switch (f) {
            case E:
              if (!message.isError()) {
                throw new IllegalArgumentException("Flag e is not set");
              }
              break;
            case P:
              if (!message.isProxiable()) {
                throw new IllegalArgumentException("Flag p is not set");
              }
              break;
            case R:
              if (!message.isRequest()) {
                throw new IllegalArgumentException("Flag m is not set");
              }
              break;
            case T:
              if (!message.isReTransmitted()) {
                throw new IllegalArgumentException("Flag t is not set");
              }
              break;
          }
        }
        // Find max constructor + lost avp set by setters
        int cacount = 0;
        Constructor<?> cm = null;
        Map<String, Class<?>> cmargs = new HashMap<String, Class<?>>();
        for (ConstructorInfo ci : c.getConstructorsInfo()) {
          if (ci.getAnnotation(Setter.class) != null) {
            // check params - all params must have avp annotation
            Class<?>[] params = ci.getConstructor().getParameterTypes();
            boolean correct = true;
            for (Class<?> j : params) {
              if (j.isArray()) {
                j = j.getComponentType();
              }
              if (storage.getClassInfo(j).getAnnotation(AvpDscr.class) == null) {
                correct = false;
                break;
              }
            }
            if (!correct) {
              continue;
            }
            // find max args constructor
            if (cacount < params.length) {
              cacount = params.length;
              cm = ci.getConstructor();
            }
          }
        }
        // fill cm args
        List<Object> initargs = new ArrayList<Object>();
        if (cm != null) {
          for (Class<?> ac : cm.getParameterTypes()) {
            Class<?> lac = ac.isArray() ? ac.getComponentType() : ac;
            cmargs.put(lac.getName(), ac);
            // Create params
            initargs.add(createChildByAvp(findChildDscr(cd.childs(), ac), ac, message.getAvps()));
          }
          // Create instance class
          rc = cm.newInstance(initargs.toArray());
        } else {
          rc = yourDomainMessageObject.newInstance();
        }
        //
        for (MethodInfo mi : c.getMethodsInfo()) {
          if (mi.getAnnotation(Setter.class) != null) {
            Class<?>[] pt = mi.getMethod().getParameterTypes();
            if (pt.length == 1 && storage.getClassInfo(pt[0]).getAnnotation(AvpDscr.class) != null) {
              Class<?> ptc = pt[0].isArray() ? pt[0].getComponentType() : pt[0];
              if (!cmargs.containsKey(ptc.getName())) {
                cmargs.put(ptc.getName(), ptc);
                mi.getMethod().invoke(rc, createChildByAvp(findChildDscr(cd.childs(), pt[0]), pt[0], message.getAvps()));
              }
            }
          }
        }
        // Fill undefined avp
        setUndefinedAvp(message.getAvps(), rc, c, cmargs);
      } catch (InstantiationException e) {
        throw new RecoderException(e);
      } catch (InvocationTargetException e) {
        throw new RecoderException(e);
      } catch (IllegalAccessException e) {
        throw new RecoderException(e);
      }
    }
    return (T) rc;
  }

  private void setUndefinedAvp(AvpSet set, Object rc, ClassInfo c, Map<String, Class<?>> cmargs) throws RecoderException {
    try {
      for (MethodInfo mi : c.getMethodsInfo()) {
        Setter s = mi.getAnnotation(Setter.class);
        if (s != null && Setter.Type.UNDEFINED.equals(s.value())) {
          Map<Integer, Integer> known = new HashMap<Integer, Integer>();
          for (Class<?> argc : cmargs.values()) {
            AvpDscr argd = storage.getClassInfo((argc.isArray() ? argc.getComponentType() : argc)).getAnnotation(AvpDscr.class);
            known.put(argd.code(), argd.code());
          }
          for (Avp a : set) {
            if (!known.containsKey(a.getCode())) {
              mi.getMethod().invoke(rc, new UnknownAvp(a.getCode(), a.isMandatory(), a.isVendorId(), a.isEncrypted(), a.getVendorId(), a.getRaw()));
            }
          }
          break;
        }
      }
    } catch (IllegalAccessException e) {
      throw new RecoderException(e);
    } catch (InvocationTargetException e) {
      throw new RecoderException(e);
    } catch (AvpDataException e) {
      throw new RecoderException(e);
    }
  }

  private Child findChildDscr(Child[] childs, Class<?> m) {
    for (Child c : childs) {
      Class<?> t = c.ref();
      m = m.isArray() ? m.getComponentType() : m;
      if (m == t) {
        return c;
      }
      if (m.getSuperclass() == t) {
        return c;
      }
      for (Class<?> i : m.getInterfaces()) {
        if (i == t) {
          return c;
        }
      }
    }
    return null;
  }

  private Object createChildByAvp(Child mInfo, Class<?> m, AvpSet parentSet)  throws RecoderException {
    Object rc;
    AvpDscr ad = storage.getClassInfo((m.isArray() ? m.getComponentType() : m)).getAnnotation(AvpDscr.class);
    Avp av = parentSet.getAvp(ad.code());
    if (av != null) {
      for (AvpFlag i : ad.must()) {
        switch (i) {
          case M:
            if (!av.isMandatory()) {
              throw new IllegalArgumentException("not set flag M");
            }
            break;
          case V:
            if (!av.isVendorId()) {
              throw new IllegalArgumentException("not set flag V");
            }
            break;
          case P:
            if (!av.isEncrypted()) {
              throw new IllegalArgumentException("not set flag P");
            }
            break;
        }
      }
    } else {
      if (mInfo.min() > 0) {
        throw new IllegalArgumentException("Avp " + ad.code() + " is mandatory");
      }
    }

    if (AvpType.Grouped.equals(ad.type())) {
      if (m.isArray()) {
        Class<?> arrayClass = m.getComponentType();
        AvpSet as = parentSet.getAvps(ad.code());
        Object[] array = (Object[]) java.lang.reflect.Array.newInstance(arrayClass, as.size());
        for (int ii = 0; ii < array.length; ii++) {
          array[ii] = newInstanceGroupedAvp(arrayClass, ad, as.getAvpByIndex(ii));
        }
        rc = array;
      } else {
        rc = newInstanceGroupedAvp(m, ad, parentSet.getAvp(ad.code()));
      }
    } else {
      if (m.isArray()) {
        Class<?> arrayClass = m.getComponentType();
        AvpSet as = parentSet.getAvps(ad.code());
        Object[] array = (Object[]) java.lang.reflect.Array.newInstance(arrayClass, as.size());
        for (int ii = 0; ii < array.length; ii++) {
          array[ii] = newInstanceSimpleAvp(arrayClass, ad, as.getAvpByIndex(ii));
        }
        rc = array;
      } else {
        rc = newInstanceSimpleAvp(m, ad, parentSet.getAvp(ad.code()));
      }

    }
    // =========
    return rc;
  }

  private Object newInstanceGroupedAvp(Class<?> m, AvpDscr ad, Avp avp) throws RecoderException {
    Object rc;
    int cacount = 0;
    ClassInfo c = storage.getClassInfo(m);
    Constructor<?> cm = null;
    Map<String, Class<?>> cmargs = new HashMap<String, Class<?>>();
    for (ConstructorInfo ci : c.getConstructorsInfo()) {
      if (ci.getAnnotation(Setter.class) != null) {
        // check params - all params must have avp annotation
        Class<?>[] params = ci.getConstructor().getParameterTypes();
        boolean correct = true;
        for (Class<?> j : params) {
          if (j.isArray()) {
            j = j.getComponentType();
          }
          if (storage.getClassInfo(j).getAnnotation(AvpDscr.class) == null) {
            correct = false;
            break;
          }
        }
        if (!correct) {
          continue;
        }
        // find max args constructor
        if (cacount < params.length) {
          cacount = params.length;
          cm = ci.getConstructor();
        }
      }
    }
    // fill cm args
    try {
      List<Object> initargs = new ArrayList<Object>();
      if (cm != null) {
        for (Class<?> ac : cm.getParameterTypes()) {
          Class<?> lac = ac.isArray() ? ac.getComponentType() : ac;
          cmargs.put(lac.getName(), ac);
          // Create params
          initargs.add(createChildByAvp(findChildDscr(ad.childs(), ac), ac, avp.getGrouped()));
        }
        // Create instance class
        rc = cm.newInstance(initargs.toArray());
      } else {
        rc = m.newInstance();
      }
      //
      for (MethodInfo mi : c.getMethodsInfo()) {
        if (mi.getAnnotation(Setter.class) != null) {
          Class<?>[] pt = mi.getMethod().getParameterTypes();
          if (pt.length == 1 && storage.getClassInfo(pt[0]).getAnnotation(AvpDscr.class) != null) {
            Class<?> ptc = pt[0].isArray() ? pt[0].getComponentType() : pt[0];
            if (!cmargs.containsKey(ptc.getName())) {
              cmargs.put(ptc.getName(), ptc);
              mi.getMethod().invoke(rc, createChildByAvp(findChildDscr(ad.childs(), pt[0]), pt[0], avp.getGrouped()));
            }
          }
        }
      }
      // Fill undefined child
      setUndefinedAvp(avp.getGrouped(), rc, c, cmargs);
    } catch (InstantiationException e) {
      throw new RecoderException(e);
    } catch (InvocationTargetException e) {
      throw new RecoderException(e);
    } catch (AvpDataException e) {
      throw new RecoderException(e);
    } catch (IllegalAccessException e) {
      throw new RecoderException(e);
    }
    return rc;
  }

  private Object newInstanceSimpleAvp(Class<?> m, AvpDscr ad, Avp avp)  {
    Object rc = null;
    if (avp == null) {
      return null;
    }
    ClassInfo c = storage.getClassInfo(m);
    try {
      for (ConstructorInfo ci : c.getConstructorsInfo()) {
        if (ci.getConstructor().getParameterTypes().length == 1 && ci.getAnnotation(Setter.class) != null) {
          List<Object> args = new ArrayList<Object>();
          if (ci.getConstructor().getParameterTypes()[0].isArray()) {
            args.add(getValue(ad.type(), avp));
          } else {
            args.add(getValue(ad.type(), avp));
          }
          rc = ci.getConstructor().newInstance(args.toArray());
        }
      }
      if (rc == null) {
        rc = m.newInstance();
        for (MethodInfo mi : c.getMethodsInfo()) {
          if (mi.getAnnotation(Setter.class) != null) {
            List<Object> args = new ArrayList<Object>();
            if (mi.getMethod().getParameterTypes()[0].isArray()) {
              args.add(getValue(ad.type(), avp));
            } else {
              args.add(getValue(ad.type(), avp));
            }
            mi.getMethod().invoke(rc, args);
          }
        }
      }
    } catch (InstantiationException e) {
      throw new RecoderException(e);
    } catch (InvocationTargetException e) {
      throw new RecoderException(e);
    } catch (AvpDataException e) {
      throw new RecoderException(e);
    } catch (IllegalAccessException e) {
      throw new RecoderException(e);
    }
    return rc;
  }

  private Object getValue(AvpType type, Avp avp) throws AvpDataException {
    switch (type) {
      case Integer32:
      case Enumerated:
        return avp.getInteger32();
      case Unsigned32:
        return avp.getUnsigned32();
      case Unsigned64:
      case Integer64:
        return avp.getInteger64();
      case Float32:
        return avp.getFloat32();
      case Float64:
        return avp.getFloat64();
      case OctetString:
      case Address:
      case Time:
      case DiameterIdentity:
      case DiameterURI:
      case IPFilterRule:
      case QoSFilterRule:
        return avp.getOctetString();
      case UTF8String:
        return avp.getUTF8String();
    }
    return null;
  }

  // =======================================================================================
}