package org.jdiameter.client.impl.annotation;

import org.jdiameter.api.*;
import org.jdiameter.api.annotation.*;
import org.jdiameter.client.impl.annotation.internal.ClassInfo;
import org.jdiameter.client.impl.annotation.internal.ConstructorInfo;
import org.jdiameter.client.impl.annotation.internal.MethodInfo;
import org.jdiameter.client.impl.annotation.internal.Storage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


// TODO full min/max/position constrains and optimization (caching)
public class Recoder {

  private Storage storage = new Storage();
  private RawSession mf;

  public Recoder(SessionFactory factory) {
    try {
      this.mf = factory.getNewRawSession();
    }
    catch (InternalException e) {
      throw new IllegalArgumentException(e);
    }
  }
  // =======================================================================================

  public Message encode( Object yourDomainMessageObject) throws Exception{
    Message message = null;
    ClassInfo c = storage.getClassInfo(yourDomainMessageObject.getClass());
    CommandDscr cd = c.getAnnotation(CommandDscr.class);
    if (cd != null) {
      // Get command parameters
      message = mf.createMessage(cd.code(), ApplicationId.createByAccAppId(cd.appId()));
      for (CommandFlag f : cd.flags()) {
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
      Map<String, Object> chMap = getChildInstance(yourDomainMessageObject, c, null);

      // Fill
      for (Child ch : cd.childs()) {
        fillChild(message.getAvps(), ch, chMap);
      }
      //
    }
    return message;
  }

  private Map<String, Object> getChildInstance(Object yourDomainMessageObject, ClassInfo c, Map<String, Object> chMap)
  throws IllegalAccessException, InvocationTargetException {
    if (chMap == null) {
      chMap = new HashMap<String, Object>();
    }

    for (MethodInfo mi : c.getMethodsInfo()) {
      if ( mi.getAnnotation(Getter.class) != null) {
        Object value = mi.getMethod().invoke(yourDomainMessageObject);
        if (value != null) {
          Class mc = value.getClass().isArray() ? value.getClass().getComponentType() : value.getClass();
          chMap.put(mc.getName(), value);
          for (Class<?> i : mc.getInterfaces()) {
            chMap.put(i.getName(), value);
          }
        }
      }
    }
    return chMap;
  }

  private void fillChild(AvpSet as, Child ci, Map<String, Object> childs) throws Exception {
    Object c = childs.get( ci.ref().getName() );
    if (c != null) {
      ClassInfo cc = storage.getClassInfo(ci.ref());
      AvpDscr ad = cc.getAnnotation(AvpDscr.class);
      if (ad != null) {
        boolean m = false, p = false;
        // cast <=> getter for primitive
        switch (ad.type()) {
        case Integer32:
        case Enumerated:
        {
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
        case Unsigned32:
        {
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
        case Integer64:
        {
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
        case Float32:
        {
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
        case Float64:
        {
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
        case QoSFilterRule:
        {
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
        case UTF8String:
        {
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
        case Grouped:
        {
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
            cv = Arrays.asList((Object[])c);
          }
          else {
            cv.add(c);
          }

          for (Object cj : cv) {
            AvpSet las = as.addGroupedAvp(ad.code(),ad.vendorId(), m, p);
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

  private <T> Collection<T> getValue(Object ic, Class<T> type ) throws Exception {
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
        if ( lm.getAnnotation(Getter.class) != null ) {
          rc.add((T)lm.getMethod().invoke(c));
        }
      }
    }
    return rc;
  }

  // =======================================================================================

  public <T> T decode( Message message,  Class<?> yourDomainMessageObject) throws Exception {
    Object rc = null;
    ClassInfo c = storage.getClassInfo(yourDomainMessageObject);
    CommandDscr cd = c.getAnnotation(CommandDscr.class);
    if (cd != null) {
      if (message.getCommandCode() != cd.code()) {
        throw new IllegalArgumentException("Invalid message code " + message.getCommandCode());
      }
      if (message.getApplicationId() != 0 && message.getApplicationId() != cd.appId()) {
        throw new IllegalArgumentException("Invalid Application-Id " + message.getApplicationId());
      }
      for (CommandFlag f : cd.flags()) {
        switch (f) {
        case E:
          if ( !message.isError() ) {
            throw new IllegalArgumentException("Flag E is not set");
          }
          break;
        case P:
          if ( !message.isProxiable() ) {
            throw new IllegalArgumentException("Flag P is not set");
          }
          break;
        case R:
          if ( !message.isRequest()) {
            throw new IllegalArgumentException("Flag M is not set");
          }
          break;
        case T:
          if ( !message.isReTransmitted() ) {
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
        if ( ci.getAnnotation(Setter.class) != null ) {
          // check params - all params must have avp annotation
          Class<?>[] params = ci.getConstructor().getParameterTypes();
          boolean correct = true;
          for (Class<?> j : params ) {
            if (j.isArray()) {
              j = j.getComponentType();
            }
            if ( storage.getClassInfo(j).getAnnotation(AvpDscr.class) == null) {
              correct = false;
              break;
            }
          }
          if ( !correct ) {
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
          initargs.add( createChildByAvp( findChildDscr(cd.childs(), ac), ac, message.getAvps()) );
        }
        // Create instance class
        rc = cm.newInstance(initargs.toArray());
      }
      else {
        rc = yourDomainMessageObject.newInstance();
      }
      //
      for (MethodInfo mi : c.getMethodsInfo()) {
        if ( mi.getAnnotation(Setter.class) != null ) {
          Class<?>[] pt = mi.getMethod().getParameterTypes();
          if (pt.length == 1 && storage.getClassInfo(pt[0]).getAnnotation(AvpDscr.class) != null) {
            Class<?> ptc = pt[0].isArray()? pt[0].getComponentType() : pt[0];
            if ( !cmargs.containsKey(ptc.getName()) ) {
              cmargs.put(ptc.getName(), ptc);
              mi.getMethod().invoke(rc, createChildByAvp( findChildDscr(cd.childs(), pt[0]), pt[0], message.getAvps()));
            }
          }
        }
      }
      // Fill undefined avp
      setUndefinedAvp(message.getAvps(), rc, c, cmargs);
    }
    return (T) rc;
  }

  private void setUndefinedAvp(AvpSet set, Object rc, ClassInfo c, Map<String, Class<?>> cmargs) throws IllegalAccessException, InvocationTargetException, AvpDataException {
    for (MethodInfo mi : c.getMethodsInfo()) {
      Setter s = mi.getAnnotation(Setter.class);
      if ( s != null && Setter.Type.UNDEFINED.equals(s.value())) {
        Map<Integer, Integer> known = new HashMap<Integer, Integer>();
        for (Class<?> argc : cmargs.values()) {
          AvpDscr argd = storage.getClassInfo( (argc.isArray() ? argc.getComponentType() : argc) ).getAnnotation(AvpDscr.class);
          known.put(argd.code(), argd.code());
        }
        for (Avp a : set) {
          if ( !known.containsKey(a.getCode())) {
            mi.getMethod().invoke(rc, new UnknownAvp(a.getCode(), a.isMandatory(), a.isVendorId(), a.isEncrypted(), a.getVendorId(), a.getRaw()));
          }
        }
        break;
      }
    }
  }

  private Child findChildDscr(Child[] childs, Class<?> m) {
    for (Child c : childs) {
      Class<?> t = c.ref();
      m = m.isArray() ? m.getComponentType() : m;
      if ( m == t ){
        return c;
      }
      if ( m.getSuperclass() == t ){
        return c;
      }
      for (Class<?> i : m.getInterfaces()) {
        if ( i == t ) {
          return c;
        }
      }
    }
    return null;
  }

  private Object createChildByAvp(Child mInfo, Class<?> m, AvpSet parentSet) throws Exception {
    Object rc = null;
    AvpDscr ad = storage.getClassInfo((m.isArray() ? m.getComponentType():m)).getAnnotation(AvpDscr.class);
    Avp av = parentSet.getAvp(ad.code());
    if (av != null) {
      for (AvpFlag i : ad.must()) {
        switch (i) {
        case M:
          if ( !av.isMandatory() ){
            throw new IllegalArgumentException("not set flag M");
          }
          break;
        case V:
          if ( !av.isVendorId() ) {
            throw new IllegalArgumentException("not set flag V");
          }
          break;
        case P:
          if ( !av.isEncrypted() ) {
            throw new IllegalArgumentException("not set flag P");
          }
          break;
        }
      }
    }
    else {
      if (mInfo.min() > 0) {
        throw new IllegalArgumentException("Avp " + ad.code() + " is mandatory");
      }
    }

    if (AvpType.Grouped.equals( ad.type()) ) {
      if (m.isArray()) {
        Class<?> arrayClass = m.getComponentType();
        AvpSet as = parentSet.getAvps(ad.code());
        Object[] array = (Object[]) java.lang.reflect.Array.newInstance(arrayClass, as.size());
        for (int ii = 0; ii < array.length; ii++) {
          array[ii] = newInstanceGroupedAvp(arrayClass, ad, as.getAvpByIndex(ii));
        }
        rc = array;
      }
      else {
        rc = newInstanceGroupedAvp(m, ad, parentSet.getAvp(ad.code()));
      }
    }
    else {
      if (m.isArray()) {
        Class<?> arrayClass = m.getComponentType();
        AvpSet as = parentSet.getAvps(ad.code());
        Object[] array = (Object[]) java.lang.reflect.Array.newInstance(arrayClass, as.size());
        for (int ii = 0; ii < array.length; ii++) {
          array[ii] = newInstanceSimpleAvp(arrayClass, ad, as.getAvpByIndex(ii));
        }
        rc = array;
      }
      else {
        rc = newInstanceSimpleAvp(m, ad, parentSet.getAvp(ad.code()));
      }

    }
    // =========
    return rc;
  }

  private Object newInstanceGroupedAvp(Class<?> m, AvpDscr ad, Avp avp) throws Exception {
    Object rc = null;
    int cacount = 0;
    ClassInfo c = storage.getClassInfo(m);
    Constructor<?> cm = null;
    Map<String, Class<?>> cmargs = new HashMap<String, Class<?>>();
    for (ConstructorInfo ci : c.getConstructorsInfo()) {
      if ( ci.getAnnotation(Setter.class) != null ) {
        // check params - all params must have avp annotation
        Class<?>[] params = ci.getConstructor().getParameterTypes();
        boolean correct = true;
        for (Class<?> j : params ) {
          if (j.isArray()) {
            j = j.getComponentType();
          }
          if ( storage.getClassInfo(j).getAnnotation(AvpDscr.class) == null) {
            correct = false;
            break;
          }
        }
        if ( !correct ) {
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
        initargs.add( createChildByAvp( findChildDscr(ad.childs(), ac), ac, avp.getGrouped()) );
      }
      // Create instance class
      rc = cm.newInstance(initargs.toArray());
    }
    else {
      rc = m.newInstance();
    }
    //
    for (MethodInfo mi : c.getMethodsInfo()) {
      if ( mi.getAnnotation(Setter.class) != null ) {
        Class<?>[] pt = mi.getMethod().getParameterTypes();
        if (pt.length == 1 && storage.getClassInfo(pt[0]).getAnnotation(AvpDscr.class) != null) {
          Class<?> ptc = pt[0].isArray()? pt[0].getComponentType() : pt[0];
          if ( !cmargs.containsKey(ptc.getName()) ) {
            cmargs.put(ptc.getName(), ptc);
            mi.getMethod().invoke(
                rc, createChildByAvp( findChildDscr(ad.childs(), pt[0]), pt[0], avp.getGrouped())
            );
          }
        }
      }
    }
    // Fill undefined child
    setUndefinedAvp(avp.getGrouped(), rc, c, cmargs);
    return rc;
  }

  private Object newInstanceSimpleAvp(Class<?> m, AvpDscr ad, Avp avp) throws Exception {
    Object rc = null;
    if (avp == null) {
      return null;
    }
    ClassInfo c = storage.getClassInfo(m);
    for (ConstructorInfo ci : c.getConstructorsInfo()) {
      if ( ci.getConstructor().getParameterTypes().length == 1 && ci.getAnnotation(Setter.class) != null ) {
        List<Object> args = new ArrayList<Object>();
        if ( ci.getConstructor().getParameterTypes()[0].isArray() ) {
          args.add( getValue(ad.type(), avp ) );
        }
        else {
          args.add( getValue(ad.type(), avp ) );
        }
        rc = ci.getConstructor().newInstance(args.toArray());
      }
    }
    if (rc == null) {
      rc = m.newInstance();
      for (MethodInfo mi : c.getMethodsInfo()) {
        if ( mi.getAnnotation(Setter.class) != null ) {
          List<Object> args = new ArrayList<Object>();
          if (  mi.getMethod().getParameterTypes()[0].isArray() ) {
            args.add( getValue(ad.type(), avp ) );
          }
          else {
            args.add( getValue(ad.type(), avp ) );
          }
          mi.getMethod().invoke(rc, args);
        }
      }
    }
    return rc;
  }

  private Object getValue(AvpType type, AvpSet avps) throws Exception {
    List<Object> all = new ArrayList<Object>();
    for (Avp a : avps) {
      all.add( getValue(type, a) );
    }
    return all.toArray();
  }

  private Object getValue(AvpType type, Avp avp) throws Exception {
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