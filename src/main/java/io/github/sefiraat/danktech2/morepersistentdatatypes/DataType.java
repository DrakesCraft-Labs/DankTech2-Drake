package io.github.sefiraat.danktech2.morepersistentdatatypes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

/**
 * Compatibility types for data written by the original DankTech2 release.
 *
 * <p>The item array format deliberately remains a BukkitObject stream. Existing
 * packs store this payload in their item PDC, so replacing it with a newer
 * library format would make a routine anti-exploit update destructive.</p>
 */
public final class DataType {

    public static final PersistentDataType<Integer, Integer> INTEGER = PersistentDataType.INTEGER;
    public static final PersistentDataType<Long, Long> LONG = PersistentDataType.LONG;
    public static final PersistentDataType<String, String> STRING = PersistentDataType.STRING;
    public static final PersistentDataType<int[], int[]> INTEGER_ARRAY = PersistentDataType.INTEGER_ARRAY;
    public static final PersistentDataType<byte[], ItemStack[]> ITEM_STACK_ARRAY = new ItemStackArrayType();

    private DataType() {
    }

    private static final class ItemStackArrayType implements PersistentDataType<byte[], ItemStack[]> {

        @Override
        public Class<byte[]> getPrimitiveType() {
            return byte[].class;
        }

        @Override
        public Class<ItemStack[]> getComplexType() {
            return ItemStack[].class;
        }

        @Override
        public byte[] toPrimitive(ItemStack[] items, PersistentDataAdapterContext context) {
            try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                 BukkitObjectOutputStream output = new BukkitObjectOutputStream(bytes)) {
                output.writeInt(items.length);
                for (ItemStack item : items) {
                    output.writeObject(item);
                }
                return bytes.toByteArray();
            } catch (IOException exception) {
                throw new IllegalStateException("Failed to serialize ItemStack[]", exception);
            }
        }

        @Override
        public ItemStack[] fromPrimitive(byte[] bytes, PersistentDataAdapterContext context) {
            try (ByteArrayInputStream inputBytes = new ByteArrayInputStream(bytes);
                 BukkitObjectInputStream input = new BukkitObjectInputStream(inputBytes)) {
                ItemStack[] items = new ItemStack[input.readInt()];
                for (int index = 0; index < items.length; index++) {
                    Object value = input.readObject();
                    items[index] = value instanceof ItemStack ? (ItemStack) value : null;
                }
                return items;
            } catch (IOException | ClassNotFoundException exception) {
                throw new IllegalStateException("Failed to deserialize ItemStack[]", exception);
            }
        }
    }
}
