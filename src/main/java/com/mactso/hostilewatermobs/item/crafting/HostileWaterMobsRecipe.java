//package com.mactso.hostilewatermobs.item.crafting;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParseException;
//import com.mactso.hostilewatermobs.Main;
//
//import net.minecraft.core.NonNullList;
//import net.minecraft.core.RegistryAccess;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.GsonHelper;
//import net.minecraft.world.inventory.CraftingContainer;
//import net.minecraft.world.item.DyeItem;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.item.crafting.Ingredient;
//import net.minecraft.world.item.crafting.RecipeSerializer;
//import net.minecraft.world.item.crafting.ShapedRecipe;
//import net.minecraft.world.item.crafting.ShapelessRecipe;
//
//public class HostileWaterMobsRecipe extends ShapelessRecipe {
//	protected final String operation;
//	private final boolean copyDamage;
//	public static final Serializer SERIALIZER = new Serializer();
//	public static final ResourceLocation NAME = new ResourceLocation(Main.MODID, "crafting_shapeless");
//
//	public HostileWaterMobsRecipe(ResourceLocation resourceLocationIn, String groupIn, ItemStack recipeOutputIn,
//			NonNullList<Ingredient> recipeItemsIn, String operationIn) {
//		// public ShapelessRecipe(ResourceLocation p_251840_, String p_249640_,
//		// CraftingBookCategory p_249390_, ItemStack p_252071_, NonNullList<Ingredient>
//		// p_250689_) {
//		super(resourceLocationIn, groupIn, null, recipeOutputIn, recipeItemsIn);
////		super(resourceLocationIn, groupIn, CraftingBookCategory.EQUIPMENT , recipeItemsIn);
//		operation = operationIn;
//		boolean copyDamage = false;
//		if (recipeOutputIn.isDamageableItem()) {
//			for (Ingredient thing : recipeItemsIn) {
//				for (ItemStack stack : thing.getItems()) {
//					if (stack.isDamageableItem() && stack.getMaxDamage() == recipeOutputIn.getMaxDamage()) {
//						copyDamage = true;
//						break;
//					}
//				}
//			}
//		}
//		this.copyDamage = copyDamage;
//	}
//
//	@Override
//	public RecipeSerializer<?> getSerializer() {
//		return SERIALIZER;
//	}
//
//	@Override
//	public ItemStack assemble(CraftingContainer inv, RegistryAccess reg) {
//		ItemStack ret = this.getResultItem(reg).copy();
//		if (copyDamage) {
//			for (int j = 0; j < inv.getContainerSize(); ++j) {
//				ItemStack itemstack = inv.getItem(j);
//				if (!itemstack.isEmpty()) {
//					if (itemstack.isDamageableItem() && itemstack.getMaxDamage() == ret.getMaxDamage()) {
//						ret.setDamageValue(itemstack.getDamageValue());
//						ret.setRepairCost(itemstack.getBaseRepairCost());
//						ret.setHoverName(itemstack.getHoverName());
//						if (itemstack.hasTag()) {
//							ret.setTag(itemstack.getTag().copy());
//						}
//						break;
//					}
//				}
//			}
//		}
//		if (operation.equals("reset_color")) {
//			CompoundTag compoundnbt = ret.getTagElement("display");
//			if (compoundnbt != null && compoundnbt.contains("color", 99))
//				compoundnbt.remove("color");
//		} else if (operation.equals("set_color")) {
//			int color = -1;
//			for (int j = 0; j < inv.getContainerSize(); ++j) {
//				ItemStack itemstack = inv.getItem(j);
//				if (!itemstack.isEmpty()) {
//					Item item = itemstack.getItem();
//					if (item instanceof DyeItem) {
//						if (item.equals(Items.WHITE_DYE)) {
//							color = 0xFFFFFF;
//						} else if (item.equals(Items.RED_DYE)) {
//							color = 0xFF0000;
//						} else if (item.equals(Items.GREEN_DYE)) {
//							color = 0x00FF00;
//						} else if (item.equals(Items.BLUE_DYE)) {
//							color = 0x0000FF;
//						} else if (item.equals(Items.BLACK_DYE)) {
//							color = 0x000000;
//						}
//						break;
//					}
//				}
//			}
//			if (color >= 0) {
//				CompoundTag compoundnbt = ret.getOrCreateTagElement("display");
//				compoundnbt.putInt("color", color);
//			} else
//				ret = ItemStack.EMPTY;
//		} else if (operation.equals("remove")) {
//			CompoundTag compoundnbt = ret.getTagElement("display");
//			if (compoundnbt != null) {
//				compoundnbt.remove("color");
//				compoundnbt.remove("glint");
//				if (compoundnbt.isEmpty())
//					ret.removeTagKey("display");
//			}
//		}
//		return ret;
//	}
//
//	public static class Serializer extends ShapelessRecipe.Serializer {
//		private static final ResourceLocation NAME = new ResourceLocation(Main.MODID, "crafting_shapeless");
//
//		@Override
////		public HostileWaterMobsRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
//			String s = GsonHelper.getAsString(json, "group", "");
//			NonNullList<Ingredient> nonnulllist = readIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
//			if (nonnulllist.isEmpty()) {
//				throw new JsonParseException("No ingredients for shapeless recipe");
////		        } else if (nonnulllist.size() > ShapedRecipe.getWidth() * ShapedRecipe.getHeight()) {
////		           throw new JsonParseException("Too many ingredients for shapeless recipe the max is " + (ShapedRecipe.getWidth() * ShapedRecipe.getHeight()));
//			} else {
//				ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
//				String s2 = GsonHelper.getAsString(json, "operation", "");
//				return new HostileWaterMobsRecipe(recipeId, s, itemstack, nonnulllist, s2);
//			}
//		}
//
//		private static NonNullList<Ingredient> readIngredients(JsonArray array) {
//			NonNullList<Ingredient> nonnulllist = NonNullList.create();
//
//			for (int i = 0; i < array.size(); ++i) {
//				Ingredient ingredient = Ingredient.fromJson(array.get(i));
//				if (!ingredient.isEmpty()) {
//					nonnulllist.add(ingredient);
//				}
//			}
//
//			return nonnulllist;
//		}
//
//		@Override
//		public HostileWaterMobsRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
//			String s = buffer.readUtf(32767);
//			int i = buffer.readVarInt();
//			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
//
//			for (int j = 0; j < nonnulllist.size(); ++j) {
//				nonnulllist.set(j, Ingredient.fromNetwork(buffer));
//			}
//
//			ItemStack itemstack = buffer.readItem();
//			String s2 = buffer.readUtf(32767);
//			return new HostileWaterMobsRecipe(recipeId, s, itemstack, nonnulllist, s2);
//		}
//
//		@Override
//		public void toNetwork(FriendlyByteBuf buffer, ShapelessRecipe recipe) {
//			super.toNetwork(buffer, recipe);
//			buffer.writeUtf(((HostileWaterMobsRecipe) recipe).operation);
//		}
//	}
//}
