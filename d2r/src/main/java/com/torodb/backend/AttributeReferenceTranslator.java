package com.torodb.backend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.torodb.backend.AttributeReference.Key;
import com.torodb.backend.AttributeReference.ObjectKey;
import com.torodb.core.TableRef;
import com.torodb.core.TableRefFactory;
import com.torodb.core.transaction.metainf.FieldType;

public class AttributeReferenceTranslator {

	public TableRef toTableRef(TableRefFactory tableRefFactory, AttributeReference attributeReference) {
		return transform(attributeReference, new TableRefTransformer(tableRefFactory));
	}

	public String toTableIdentifier(AttributeReference attributeReference, String collection) {
		return transform(attributeReference, new TableNameTransformer(collection));
	}

	public String toColumnName(AttributeReference attributeReference, FieldType fieldType) {
		return transform(fromLastObjectKey(attributeReference), new ColumnNameTransformer(fieldType));
	}

	private ObjectKey getLastObjectKey(AttributeReference attributeReference) {
		List<Key> keys = attributeReference.getKeys();
		for (int index = keys.size() - 1; index >= 0; index--) {
			Key key = keys.get(index);
			if (key instanceof ObjectKey) {
				return (ObjectKey) key;
			}
		}
		return null;
	}

	private AttributeReference fromLastObjectKey(AttributeReference attributeReference) {
		List<Key> keys = attributeReference.getKeys();
		List<Key> fromLastObjectKeyKeys = new ArrayList<>(keys.size());
		for (int index = keys.size() - 1; index >= 0; index--) {
			Key key = keys.get(index);
			fromLastObjectKeyKeys.add(0, key);
			if (key instanceof ObjectKey) {
				break;
			}
		}
		return new AttributeReference(fromLastObjectKeyKeys);
	}

	private <T> T transform(AttributeReference attributeReference, Transformer<T> transformer) {
		Iterator<Key> keysIterator = attributeReference.getKeys().iterator();
		int level = 0;
		while (keysIterator.hasNext()) {
			Key key = keysIterator.next();
			if (key instanceof ObjectKey) {
				transformer.append(((ObjectKey) key).getKey());
				level = 0;
			} else {
				level++;
				if (level > 1) {
					transformer.append(level);
				}
			}
		}
		return transformer.getTransformation();
	}

	private interface Transformer<T> {
		void append(String key);

		void append(int level);

		T getTransformation();
	}

	private static class TableRefTransformer implements Transformer<TableRef> {
	    private final TableRefFactory tableRefFactory;
	    
		private TableRef tableRef;

		private TableRefTransformer(TableRefFactory tableRefFactory) {
            super();
            this.tableRefFactory = tableRefFactory;
            this.tableRef = tableRefFactory.createRoot();
        }

        @Override
		public void append(String key) {
			tableRef = tableRefFactory.createChild(tableRef, key);
		}

		@Override
		public void append(int level) {
			tableRef = tableRefFactory.createChild(tableRef, "$" + level);
		}

		@Override
		public TableRef getTransformation() {
			return tableRef;
		}
	}

	private static class TableNameTransformer implements Transformer<String> {
		private final StringBuilder tableNameBuilder = new StringBuilder();

		public TableNameTransformer(String collection) {
			append(collection);
		}

		@Override
		public void append(String key) {
			tableNameBuilder.append(key.toLowerCase(Locale.US).replaceAll("[^a-z0-9$]", "_")).append('_');
		}

		@Override
		public void append(int level) {
			int charsToDelete = 1;
			if (level > 2) {
				charsToDelete += 1 + String.valueOf(level - 1).length();
			}
			tableNameBuilder.delete(tableNameBuilder.length() - charsToDelete, tableNameBuilder.length());
			tableNameBuilder.append('$').append(level).append('_');
		}

		@Override
		public String getTransformation() {
			tableNameBuilder.deleteCharAt(tableNameBuilder.length() - 1);
			return tableNameBuilder.toString().replaceAll("_+", "_");
		}
	}

	private static class ColumnNameTransformer implements Transformer<String> {

		/*
		 * 0 BINARY, 1 BOOLEAN, 2 DATE, 3 DOUBLE, 4 INSTANT, 5 INTEGER, 6 LONG,
		 * 7 MONGO_OBJECT_ID, 8 MONGO_TIME_STAMP, 9 NULL, 10 STRING, 11 TIME, 12
		 * CHILD;
		 */
		private static final char[] FIELD_TYPE_IDENTIFIERS = new char[] { 'r', 'b', 't', 'd', 'k', 'i', 'l', 'x', 'y',
				'n', 's', 'c', 'e' };
		private final FieldType fieldType;
		private final StringBuilder columnNameBuilder = new StringBuilder();

		public ColumnNameTransformer(FieldType fieldType) {
			this.fieldType = fieldType;
		}

		@Override
		public void append(String key) {
			columnNameBuilder.append(key.toLowerCase(Locale.US).replaceAll("[^a-z0-9$]", "_")).append('_');
		}

		@Override
		public void append(int level) {
			int charsToDelete = 1;
			if (level > 2) {
				charsToDelete += 1 + String.valueOf(level - 1).length();
			}
			columnNameBuilder.delete(columnNameBuilder.length() - charsToDelete, columnNameBuilder.length());
			columnNameBuilder.append('$').append(level).append('_');
		}

		@Override
		public String getTransformation() {
			columnNameBuilder.append(FIELD_TYPE_IDENTIFIERS[fieldType.ordinal()]);
			return columnNameBuilder.toString();
		}
	}
}
