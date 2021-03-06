package com.notjuststudio.fpnt;

import com.notjuststudio.threadsauce.ConcurrentHashSet;
import com.sun.istack.internal.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author KLLdon
 */
public class FPNTContainer {

    private final Map<Byte, Map<String, Object>> maps = new ConcurrentHashMap<>();
    private final Set<FPNTExpander> expanderSet = new ConcurrentHashSet<>();
    private final Set<FPNTHandler> handlerSet = new ConcurrentHashSet<>();
    private int version = 0;

    /**
     * Get version
     * @return
     */
    public int getVersion() {
        return version;
    }

    /**
     * Set version
     * @param version
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Empty constructor
     */
    public FPNTContainer() {}

    /**
     * Constructor with custom Expander
     * @param expander
     */
    public FPNTContainer(@NotNull final FPNTExpander expander) {
        addExpander(expander);
    }

    /**
     * Constructor with custom ExpanderList
     * @param expanderSet
     */
    public FPNTContainer(@NotNull final Set<FPNTExpander> expanderSet) {
        this.expanderSet.addAll(expanderSet);
    }

    /**
     * Get ExpanderList
     * @return
     */
    public Set<FPNTExpander> getExpanderSet() {
        return new HashSet<>(expanderSet);
    }

    /**
     * Add custom Expander
     * @param expander
     */
    public void addExpander(@NotNull final FPNTExpander expander) {
        this.expanderSet.add(expander);
    }

    /**
     * Remove custom Expander
     * @param expander
     */
    public void removeExpander(@NotNull final FPNTExpander expander) {
        this.expanderSet.remove(expander);
    }

    /**
     * Remove all Expander
     */
    public void clearExpanderList() {
        expanderSet.clear();
    }

    /**
     * Get HandlerList
     * @return
     */
    public Set<FPNTHandler> getHandlerSet() {
        return new HashSet<>(handlerSet);
    }

    /**
     * Add custom Handler
     * @param handler
     */
    public void addHandler(@NotNull final FPNTHandler handler) {
        this.handlerSet.add(handler);
    }

    /**
     * Remove custom Handler
     * @param handler
     */
    public void removeHandler(@NotNull final FPNTHandler handler) {
        this.handlerSet.remove(handler);
    }

    /**
     * Remove all Handler
     */
    public void clearHandlerList() {
        handlerSet.clear();
    }

    /**
     * Put value in type map by key
     * @param type byte key for map
     * @param key
     * @param value
     * @return this
     */
    public FPNTContainer put(@NotNull final byte type, @NotNull final String key, @NotNull final Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Value must be not null");
        }
        maps.computeIfAbsent(type, k -> new ConcurrentHashMap<>());
        final Object old = maps.get(type).put(key, value);
        for (FPNTHandler handler : handlerSet) {
            handler.handle(this, type, key, old, value);
        }
        return this;
    }

    /**
     * Get value by key
     * @param type byte key
     * @param key
     * @return value
     */
    public Object get(@NotNull final byte type, @NotNull final String key) {
        final Map<String, Object> map = maps.get(type);
        return map == null ? null : map.get(key);
    }

    /**
     * Get value by key or return default value
     * @param type byte key
     * @param key
     * @param value default
     * @return value
     */
    public Object getOrDefault(@NotNull final byte type, @NotNull final String key, final Object value) {
        final Map<String, Object> map = maps.get(type);
        if (map == null)
            return value;
        final Object tmp = map.get(key);
        return tmp == null ? value : tmp;
    }

    /**
     * Return true, if contains value of type in key
     * @param type byte key of value
     * @param key
     * @return
     */
    public boolean contains(@NotNull final byte type, @NotNull final String key) {
        if (maps.containsKey(type)) {
            return  maps.get(type).containsKey(key);
        } else {
            return false;
        }
    }

    /**
     * Remove and return value by key
     * @param type byte key
     * @param key
     * @return value
     */
    public Object remove(@NotNull final byte type, @NotNull final String key) {
        final Map<String, Object> tmpMap = maps.get(type);
        final Object tmpValue;
        if (tmpMap == null) {
            tmpValue = null;
        } else {
            tmpValue = tmpMap.remove(key);
            if (tmpMap.isEmpty())
                maps.remove(type);
        }
        for (FPNTHandler handler : handlerSet) {
            handler.handle(this, type, key, tmpValue, null);
        }
        return tmpValue;
    }

    /**
     * Get map of maps
     * @return
     */
    public Map<Byte, Map<String, Object>> getMaps() {
        return maps;
    }

    /**
     * Get available types
     * @return
     */
    public Set<Byte> getTypes() {
        return maps.keySet();
    }

    /**
     * Get map of type
     * @param type byte key
     * @return
     */
    public Map<String, Object> getTypeMap(@NotNull final byte type) {
        maps.computeIfAbsent(type, k -> new ConcurrentHashMap<>());
        return maps.get(type);
    }

    /**
     * Get available keys of type
     * @param type byte key
     * @return
     */
    public Set<String> getTypeKeys(@NotNull final byte type) {
        return getTypeMap(type).keySet();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("[");
        final Iterator<Map.Entry<Byte, Map<String, Object>>> iterator = maps.entrySet().iterator();
        while(iterator.hasNext()) {
            final Map.Entry<Byte, Map<String, Object>> entry = iterator.next();
            final String name;
            switch (entry.getKey()) {
                case FPNTConstants.BOOLEAN: {
                    name = "Boolean";
                    break;
                }
                case FPNTConstants.BYTE: {
                    name = "Byte";
                    break;
                }
                case FPNTConstants.CHAR: {
                    name = "Character";
                    break;
                }
                case FPNTConstants.INT: {
                    name = "Integer";
                    break;
                }
                case FPNTConstants.LONG: {
                    name = "Long";
                    break;
                }
                case FPNTConstants.BOOLEAN_ARRAY: {
                    name = "Boolean[]";
                    break;
                }
                case FPNTConstants.BYTE_ARRAY: {
                    name = "Byte[]";
                    break;
                }
                case FPNTConstants.CHAR_ARRAY: {
                    name = "Character[]";
                    break;
                }
                case FPNTConstants.INT_ARRAY: {
                    name = "Integer[]";
                    break;
                }
                case FPNTConstants.LONG_ARRAY: {
                    name = "Long[]";
                    break;
                }
                default: {
                    name = Byte.toString(entry.getKey());
                }
            }
            builder.append(name).append(":").append(entry.getValue().size());
            if (iterator.hasNext())
                builder.append(",");
        }
        builder.append("]");
        return builder.toString();
    }
}
