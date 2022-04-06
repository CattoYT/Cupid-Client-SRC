package us.myles.ViaVersion.api.type;

import java.util.UUID;
import us.myles.ViaVersion.api.minecraft.BlockChangeRecord;
import us.myles.ViaVersion.api.minecraft.EulerAngle;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.api.minecraft.Vector;
import us.myles.ViaVersion.api.minecraft.VillagerData;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.type.types.ArrayType;
import us.myles.ViaVersion.api.type.types.BooleanType;
import us.myles.ViaVersion.api.type.types.ByteArrayType;
import us.myles.ViaVersion.api.type.types.ByteType;
import us.myles.ViaVersion.api.type.types.ComponentType;
import us.myles.ViaVersion.api.type.types.DoubleType;
import us.myles.ViaVersion.api.type.types.FloatType;
import us.myles.ViaVersion.api.type.types.IntType;
import us.myles.ViaVersion.api.type.types.LongType;
import us.myles.ViaVersion.api.type.types.RemainingBytesType;
import us.myles.ViaVersion.api.type.types.ShortType;
import us.myles.ViaVersion.api.type.types.StringType;
import us.myles.ViaVersion.api.type.types.UUIDIntArrayType;
import us.myles.ViaVersion.api.type.types.UUIDType;
import us.myles.ViaVersion.api.type.types.UnsignedByteType;
import us.myles.ViaVersion.api.type.types.UnsignedShortType;
import us.myles.ViaVersion.api.type.types.VarIntArrayType;
import us.myles.ViaVersion.api.type.types.VarIntType;
import us.myles.ViaVersion.api.type.types.VarLongType;
import us.myles.ViaVersion.api.type.types.VoidType;
import us.myles.ViaVersion.api.type.types.minecraft.BlockChangeRecordType;
import us.myles.ViaVersion.api.type.types.minecraft.EulerAngleType;
import us.myles.ViaVersion.api.type.types.minecraft.FlatItemArrayType;
import us.myles.ViaVersion.api.type.types.minecraft.FlatItemType;
import us.myles.ViaVersion.api.type.types.minecraft.FlatVarIntItemArrayType;
import us.myles.ViaVersion.api.type.types.minecraft.FlatVarIntItemType;
import us.myles.ViaVersion.api.type.types.minecraft.ItemArrayType;
import us.myles.ViaVersion.api.type.types.minecraft.ItemType;
import us.myles.ViaVersion.api.type.types.minecraft.NBTType;
import us.myles.ViaVersion.api.type.types.minecraft.OptPosition1_14Type;
import us.myles.ViaVersion.api.type.types.minecraft.OptPositionType;
import us.myles.ViaVersion.api.type.types.minecraft.OptUUIDType;
import us.myles.ViaVersion.api.type.types.minecraft.OptionalComponentType;
import us.myles.ViaVersion.api.type.types.minecraft.OptionalVarIntType;
import us.myles.ViaVersion.api.type.types.minecraft.Position1_14Type;
import us.myles.ViaVersion.api.type.types.minecraft.PositionType;
import us.myles.ViaVersion.api.type.types.minecraft.VarLongBlockChangeRecordType;
import us.myles.ViaVersion.api.type.types.minecraft.VectorType;
import us.myles.ViaVersion.api.type.types.minecraft.VillagerDataType;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;

public abstract class Type<T> implements ByteBufReader<T>, ByteBufWriter<T> {
  public static final Type<Byte> BYTE = (Type<Byte>)new ByteType();
  
  @Deprecated
  public static final Type<Byte[]> BYTE_ARRAY = (Type<Byte[]>)new ArrayType(BYTE);
  
  public static final Type<byte[]> BYTE_ARRAY_PRIMITIVE = (Type<byte[]>)new ByteArrayType();
  
  public static final Type<byte[]> REMAINING_BYTES = (Type<byte[]>)new RemainingBytesType();
  
  public static final Type<Short> UNSIGNED_BYTE = (Type<Short>)new UnsignedByteType();
  
  @Deprecated
  public static final Type<Short[]> UNSIGNED_BYTE_ARRAY = (Type<Short[]>)new ArrayType(UNSIGNED_BYTE);
  
  public static final Type<Boolean> BOOLEAN = (Type<Boolean>)new BooleanType();
  
  @Deprecated
  public static final Type<Boolean[]> BOOLEAN_ARRAY = (Type<Boolean[]>)new ArrayType(BOOLEAN);
  
  public static final Type<Integer> INT = (Type<Integer>)new IntType();
  
  @Deprecated
  public static final Type<Integer[]> INT_ARRAY = (Type<Integer[]>)new ArrayType(INT);
  
  public static final Type<Double> DOUBLE = (Type<Double>)new DoubleType();
  
  @Deprecated
  public static final Type<Double[]> DOUBLE_ARRAY = (Type<Double[]>)new ArrayType(DOUBLE);
  
  public static final Type<Long> LONG = (Type<Long>)new LongType();
  
  @Deprecated
  public static final Type<Long[]> LONG_ARRAY = (Type<Long[]>)new ArrayType(LONG);
  
  public static final FloatType FLOAT = new FloatType();
  
  @Deprecated
  public static final Type<Float[]> FLOAT_ARRAY = (Type<Float[]>)new ArrayType((Type)FLOAT);
  
  public static final ShortType SHORT = new ShortType();
  
  @Deprecated
  public static final Type<Short[]> SHORT_ARRAY = (Type<Short[]>)new ArrayType((Type)SHORT);
  
  public static final Type<Integer> UNSIGNED_SHORT = (Type<Integer>)new UnsignedShortType();
  
  @Deprecated
  public static final Type<Integer[]> UNSIGNED_SHORT_ARRAY = (Type<Integer[]>)new ArrayType(UNSIGNED_SHORT);
  
  public static final Type<JsonElement> COMPONENT = (Type<JsonElement>)new ComponentType();
  
  public static final Type<String> STRING = (Type<String>)new StringType();
  
  public static final Type<String[]> STRING_ARRAY = (Type<String[]>)new ArrayType(STRING);
  
  public static final Type<UUID> UUID = (Type<UUID>)new UUIDType();
  
  public static final Type<UUID> UUID_INT_ARRAY = (Type<UUID>)new UUIDIntArrayType();
  
  public static final Type<UUID[]> UUID_ARRAY = (Type<UUID[]>)new ArrayType(UUID);
  
  public static final VarIntType VAR_INT = new VarIntType();
  
  @Deprecated
  public static final Type<Integer[]> VAR_INT_ARRAY = (Type<Integer[]>)new ArrayType((Type)VAR_INT);
  
  public static final Type<int[]> VAR_INT_ARRAY_PRIMITIVE = (Type<int[]>)new VarIntArrayType();
  
  public static final Type<Integer> OPTIONAL_VAR_INT = (Type<Integer>)new OptionalVarIntType();
  
  public static final VarLongType VAR_LONG = new VarLongType();
  
  @Deprecated
  public static final Type<Long[]> VAR_LONG_ARRAY = (Type<Long[]>)new ArrayType((Type)VAR_LONG);
  
  public static final Type<Void> NOTHING = (Type<Void>)new VoidType();
  
  public static final Type<Position> POSITION = (Type<Position>)new PositionType();
  
  public static final Type<Position> POSITION1_14 = (Type<Position>)new Position1_14Type();
  
  public static final Type<EulerAngle> ROTATION = (Type<EulerAngle>)new EulerAngleType();
  
  public static final Type<Vector> VECTOR = (Type<Vector>)new VectorType();
  
  public static final Type<CompoundTag> NBT = (Type<CompoundTag>)new NBTType();
  
  public static final Type<CompoundTag[]> NBT_ARRAY = (Type<CompoundTag[]>)new ArrayType(NBT);
  
  public static final Type<UUID> OPTIONAL_UUID = (Type<UUID>)new OptUUIDType();
  
  public static final Type<JsonElement> OPTIONAL_COMPONENT = (Type<JsonElement>)new OptionalComponentType();
  
  public static final Type<Position> OPTIONAL_POSITION = (Type<Position>)new OptPositionType();
  
  public static final Type<Position> OPTIONAL_POSITION_1_14 = (Type<Position>)new OptPosition1_14Type();
  
  public static final Type<Item> ITEM = (Type<Item>)new ItemType();
  
  public static final Type<Item[]> ITEM_ARRAY = (Type<Item[]>)new ItemArrayType();
  
  public static final Type<BlockChangeRecord> BLOCK_CHANGE_RECORD = (Type<BlockChangeRecord>)new BlockChangeRecordType();
  
  public static final Type<BlockChangeRecord[]> BLOCK_CHANGE_RECORD_ARRAY = (Type<BlockChangeRecord[]>)new ArrayType(BLOCK_CHANGE_RECORD);
  
  public static final Type<BlockChangeRecord> VAR_LONG_BLOCK_CHANGE_RECORD = (Type<BlockChangeRecord>)new VarLongBlockChangeRecordType();
  
  public static final Type<BlockChangeRecord[]> VAR_LONG_BLOCK_CHANGE_RECORD_ARRAY = (Type<BlockChangeRecord[]>)new ArrayType(VAR_LONG_BLOCK_CHANGE_RECORD);
  
  public static final Type<VillagerData> VILLAGER_DATA = (Type<VillagerData>)new VillagerDataType();
  
  public static final Type<Item> FLAT_ITEM = (Type<Item>)new FlatItemType();
  
  public static final Type<Item> FLAT_VAR_INT_ITEM = (Type<Item>)new FlatVarIntItemType();
  
  public static final Type<Item[]> FLAT_ITEM_ARRAY = (Type<Item[]>)new FlatItemArrayType();
  
  public static final Type<Item[]> FLAT_VAR_INT_ITEM_ARRAY = (Type<Item[]>)new FlatVarIntItemArrayType();
  
  public static final Type<Item[]> FLAT_ITEM_ARRAY_VAR_INT = (Type<Item[]>)new ArrayType(FLAT_ITEM);
  
  public static final Type<Item[]> FLAT_VAR_INT_ITEM_ARRAY_VAR_INT = (Type<Item[]>)new ArrayType(FLAT_VAR_INT_ITEM);
  
  private final Class<? super T> outputClass;
  
  private final String typeName;
  
  public Type(Class<? super T> outputClass) {
    this(outputClass.getSimpleName(), outputClass);
  }
  
  public Type(String typeName, Class<? super T> outputClass) {
    this.outputClass = outputClass;
    this.typeName = typeName;
  }
  
  public Class<? super T> getOutputClass() {
    return this.outputClass;
  }
  
  public String getTypeName() {
    return this.typeName;
  }
  
  public Class<? extends Type> getBaseClass() {
    return (Class)getClass();
  }
  
  public String toString() {
    return "Type|" + this.typeName;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\Type.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */