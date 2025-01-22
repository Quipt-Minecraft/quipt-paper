package me.quickscythe.quipt.simple_json;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class SimpleJsonArray implements Iterable<Object> {

    private final ArrayList<Object> myArrayList;

    public SimpleJsonArray() {
        this.myArrayList = new ArrayList<Object>();
    }


    public SimpleJsonArray(Collection<?> collection) {
        if (collection == null) {
            this.myArrayList = new ArrayList<Object>();
        } else {
            this.myArrayList = new ArrayList<Object>(collection.size());
            this.addAll(collection, true);
        }
    }


    public SimpleJsonArray(Object array) throws SimpleJsonException {
        this();
        if (!array.getClass().isArray()) {
            throw new SimpleJsonException("JSONArray initial value should be a string or collection or array.");
        }
        this.addAll(array, true);
    }

    @Override
    public Iterator<Object> iterator() {
        return this.myArrayList.iterator();
    }

    private void addAll(Iterable<?> iter, boolean wrap) {
        if (wrap) {
            for (Object o : iter) {
                this.put(SimpleJsonObject.wrap(o));
            }
        } else {
            for (Object o : iter) {
                this.put(o);
            }
        }
    }

    private void addAll(Object array, boolean wrap) throws SimpleJsonException {
        if (array.getClass().isArray()) {
            int length = Array.getLength(array);
            this.myArrayList.ensureCapacity(this.myArrayList.size() + length);
            if (wrap) {
                for (int i = 0; i < length; i += 1) {
                    this.put(SimpleJsonObject.wrap(Array.get(array, i)));
                }
            } else {
                for (int i = 0; i < length; i += 1) {
                    this.put(Array.get(array, i));
                }
            }
        } else if (array instanceof SimpleJsonArray) {
            // use the built in array list `addAll` as all object
            // wrapping should have been completed in the original
            // JSONArray
            this.myArrayList.addAll(((SimpleJsonArray) array).myArrayList);
        } else if (array instanceof Collection) {
            this.addAll((Collection<?>) array, wrap);
        } else if (array instanceof Iterable) {
            this.addAll((Iterable<?>) array, wrap);
        } else {
            throw new SimpleJsonException("JSONArray initial value should be a string or collection or array.");
        }
    }

    public SimpleJsonArray put(boolean value) {
        return this.put(value ? Boolean.TRUE : Boolean.FALSE);
    }


    public SimpleJsonArray put(Collection<?> value) {
        return this.put(new SimpleJsonArray(value));
    }

    public SimpleJsonArray put(double value) throws SimpleJsonException {
        return this.put(Double.valueOf(value));
    }

    public SimpleJsonArray put(float value) throws SimpleJsonException {
        return this.put(Float.valueOf(value));
    }

    public SimpleJsonArray put(int value) {
        return this.put(Integer.valueOf(value));
    }

    public SimpleJsonArray put(long value) {
        return this.put(Long.valueOf(value));
    }

    public SimpleJsonArray put(Map<?, ?> value) {
        return this.put(new SimpleJsonObject(value));
    }

    public SimpleJsonArray put(Object value) {
        SimpleJsonObject.testValidity(value);
        this.myArrayList.add(value);
        return this;
    }

    public SimpleJsonArray put(int index, boolean value) throws SimpleJsonException {
        return this.put(index, value ? Boolean.TRUE : Boolean.FALSE);
    }

    public SimpleJsonArray put(int index, Collection<?> value) throws SimpleJsonException {
        return this.put(index, new SimpleJsonArray(value));
    }

    public SimpleJsonArray put(int index, double value) throws SimpleJsonException {
        return this.put(index, Double.valueOf(value));
    }

    public SimpleJsonArray put(int index, float value) throws SimpleJsonException {
        return this.put(index, Float.valueOf(value));
    }

    public SimpleJsonArray put(int index, int value) throws SimpleJsonException {
        return this.put(index, Integer.valueOf(value));
    }

    public SimpleJsonArray put(int index, long value) throws SimpleJsonException {
        return this.put(index, Long.valueOf(value));
    }

    public SimpleJsonArray put(int index, Map<?, ?> value) throws SimpleJsonException {
        this.put(index, new SimpleJsonObject(value));
        return this;
    }

    public SimpleJsonArray put(int index, Object value) throws SimpleJsonException {
        if (index < 0) {
            throw new SimpleJsonException("JSONArray[" + index + "] not found.");
        }
        if (index < this.length()) {
            SimpleJsonObject.testValidity(value);
            this.myArrayList.set(index, value);
            return this;
        }
        if (index == this.length()) {
            return this.put(value);
        }
        this.myArrayList.ensureCapacity(index + 1);
        while (index != this.length()) {
            this.myArrayList.add(SimpleJsonObject.NULL);
        }
        return this.put(value);
    }

    public int length() {
        return this.myArrayList.size();
    }

    public Writer write(Writer writer, int indentFactor, int indent) throws SimpleJsonException {
        try {
            boolean needsComma = false;
            int length = this.length();
            writer.write('[');

            if (length == 1) {
                try {
                    SimpleJsonObject.writeValue(writer, this.myArrayList.get(0), indentFactor, indent);
                } catch (Exception e) {
                    throw new SimpleJsonException("Unable to write JSONArray value at index: 0", e);
                }
            } else if (length != 0) {
                final int newIndent = indent + indentFactor;

                for (int i = 0; i < length; i += 1) {
                    if (needsComma) {
                        writer.write(',');
                    }
                    if (indentFactor > 0) {
                        writer.write('\n');
                    }
                    SimpleJsonObject.indent(writer, newIndent);
                    try {
                        SimpleJsonObject.writeValue(writer, this.myArrayList.get(i), indentFactor, newIndent);
                    } catch (Exception e) {
                        throw new SimpleJsonException("Unable to write JSONArray value at index: " + i, e);
                    }
                    needsComma = true;
                }
                if (indentFactor > 0) {
                    writer.write('\n');
                }
                SimpleJsonObject.indent(writer, indent);
            }
            writer.write(']');
            return writer;
        } catch (IOException e) {
            throw new SimpleJsonException(e);
        }
    }
}
