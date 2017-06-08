package com.jagex.runescape.cache.def;

import com.jagex.runescape.Archive;
import com.jagex.runescape.Buffer;
import com.jagex.runescape.Game;
import com.jagex.runescape.cache.cfg.Varbit;
import com.jagex.runescape.collection.Cache;
import com.jagex.runescape.media.Animation;
import com.jagex.runescape.media.renderable.Model;

public class ActorDefinition {


	public int standAnimationId = -1;
	public int childrenIds[];
	public int headModelIndexes[];
	public int modelIds[];
	public long id = -1L;
	public static Game client;
	public int sizeY = 128;
	public boolean clickable = true;
	public int sizeXZ = 128;
	public int turnLeftAnimationId = -1;
	public int modifiedModelColors[];
	public static Cache modelCache = new Cache(30);
	public boolean minimapVisible = true;
	public int headIcon = -1;
	public int combatLevel = -1;
	public int turnRightAnimationId = -1;
	public byte boundaryDimension = 1;
	public int turnAroundAnimationId = -1;
	public boolean visible = false;
	public int walkAnimationId = -1;
	public String actions[];
	public static int size;
	public static int bufferOffsets[];
	public int degreesToTurn = 32;
	public String name = "null";
	public int varBitId = -1;
	public static ActorDefinition cache[];
	public int originalModelColors[];
	public static Buffer buffer;
	public int contrast;
	public int settingId = -1;
	public byte description[];
	public static int bufferIndex;
	public boolean aBoolean662 = false;
	public int brightness;


	public static ActorDefinition getDefinition(int id) {
		for (int i = 0; i < 20; i++) {
			if (ActorDefinition.cache[i].id == id) {
				return ActorDefinition.cache[i];
			}
		}
		ActorDefinition.bufferIndex = (ActorDefinition.bufferIndex + 1) % 20;
		ActorDefinition definition = ActorDefinition.cache[ActorDefinition.bufferIndex] = new ActorDefinition();
		ActorDefinition.buffer.offset = ActorDefinition.bufferOffsets[id];
		definition.id = id;
		definition.loadDefinition(ActorDefinition.buffer);
		return definition;
	}


	public void loadDefinition(Buffer buffer) {
		while (true) {
			int attributeId = buffer.getUnsignedByte();
			if (attributeId == 0)
				return;
			if (attributeId == 1) {
				int modelCount = buffer.getUnsignedByte();
				modelIds = new int[modelCount];
				for (int model = 0; model < modelCount; model++)
					modelIds[model] = buffer.getUnsignedLEShort();

			} else if (attributeId == 2)
				name = buffer.getString();
			else if (attributeId == 3)
				description = buffer.getStringBytes();
			else if (attributeId == 12)
				boundaryDimension = buffer.getSignedByte();
			else if (attributeId == 13)
				standAnimationId = buffer.getUnsignedLEShort();
			else if (attributeId == 14)
				walkAnimationId = buffer.getUnsignedLEShort();
			else if (attributeId == 17) {
				walkAnimationId = buffer.getUnsignedLEShort();
				turnAroundAnimationId = buffer.getUnsignedLEShort();
				turnRightAnimationId = buffer.getUnsignedLEShort();
				turnLeftAnimationId = buffer.getUnsignedLEShort();
			} else if (attributeId >= 30 && attributeId < 40) {
				if (actions == null)
					actions = new String[5];
				actions[attributeId - 30] = buffer.getString();
				if (actions[attributeId - 30].equalsIgnoreCase("hidden"))
					actions[attributeId - 30] = null;
			} else if (attributeId == 40) {
				int modelColorCount = buffer.getUnsignedByte();
				modifiedModelColors = new int[modelColorCount];
				originalModelColors = new int[modelColorCount];
				for (int color = 0; color < modelColorCount; color++) {
					modifiedModelColors[color] = buffer.getUnsignedLEShort();
					originalModelColors[color] = buffer.getUnsignedLEShort();
				}

			} else if (attributeId == 60) {
				int additionalModelCount = buffer.getUnsignedByte();
				headModelIndexes = new int[additionalModelCount];
				for (int model = 0; model < additionalModelCount; model++)
					headModelIndexes[model] = buffer.getUnsignedLEShort();

			} else if (attributeId == 90)
				buffer.getUnsignedLEShort(); // dummy
			else if (attributeId == 91)
				buffer.getUnsignedLEShort(); // dummy
			else if (attributeId == 92)
				buffer.getUnsignedLEShort(); // dummy
			else if (attributeId == 93)
				minimapVisible = false;
			else if (attributeId == 95)
				combatLevel = buffer.getUnsignedLEShort();
			else if (attributeId == 97)
				sizeXZ = buffer.getUnsignedLEShort();
			else if (attributeId == 98)
				sizeY = buffer.getUnsignedLEShort();
			else if (attributeId == 99)
				visible = true;
			else if (attributeId == 100)
				brightness = buffer.getSignedByte();
			else if (attributeId == 101)
				contrast = buffer.getSignedByte() * 5;
			else if (attributeId == 102)
				headIcon = buffer.getUnsignedLEShort();
			else if (attributeId == 103)
				degreesToTurn = buffer.getUnsignedLEShort();
			else if (attributeId == 106) {
				varBitId = buffer.getUnsignedLEShort();
				if (varBitId == 65535)
					varBitId = -1;
				settingId = buffer.getUnsignedLEShort();
				if (settingId == 65535)
					settingId = -1;
				int childrenCount = buffer.getUnsignedByte();
				childrenIds = new int[childrenCount + 1];
				for (int child = 0; child <= childrenCount; child++) {
					childrenIds[child] = buffer.getUnsignedLEShort();
					if (childrenIds[child] == 65535)
						childrenIds[child] = -1;
				}

			} else if (attributeId == 107)
				clickable = false;
		}
	}

	public static void reset() {
		ActorDefinition.modelCache = null;
		ActorDefinition.bufferOffsets = null;
		ActorDefinition.cache = null;
		ActorDefinition.buffer = null;
	}

	public Model getHeadModel() {
		if (childrenIds != null) {
			ActorDefinition definition = getChildDefinition();
			if (definition == null)
				return null;
			else
				return definition.getHeadModel();
		}
		if (headModelIndexes == null)
			return null;
		boolean cached = false;
		for (int headModel = 0; headModel < headModelIndexes.length; headModel++)
			if (!Model.isCached(headModelIndexes[headModel]))
				cached = true;

		if (cached)
			return null;
		Model[] headModels = new Model[headModelIndexes.length];
		for (int model = 0; model < headModelIndexes.length; model++)
			headModels[model] = Model.getModel(headModelIndexes[model]);

		Model headModel;
		if (headModels.length == 1)
			headModel = headModels[0];
		else
			headModel = new Model(headModels.length,
					headModels);
		if (modifiedModelColors != null) {
			for (int color = 0; color < modifiedModelColors.length; color++)
				headModel.replaceColor(modifiedModelColors[color], originalModelColors[color]);

		}
		return headModel;
	}

	public boolean method360(int i) {
		while (i >= 0)
			aBoolean662 = !aBoolean662;
		if (childrenIds == null)
			return true;
		int j = -1;
		if (varBitId != -1) {
			Varbit class49 = Varbit.cache[varBitId];
			int k = class49.configId;
			int l = class49.leastSignificantBit;
			int i1 = class49.mostSignificantBit;
			int j1 = client.BITFIELD_MAX_VALUE[i1 - l];
			j = client.widgetSettings[k] >> l & j1;
		} else if (settingId != -1)
			j = client.widgetSettings[settingId];
		return j >= 0 && j < childrenIds.length && childrenIds[j] != -1;
	}

	public static void load(Archive archive) {
		ActorDefinition.buffer = new Buffer(archive.getFile("npc.dat"));
		Buffer buffer = new Buffer(archive.getFile("npc.idx"));
		ActorDefinition.size = buffer.getUnsignedLEShort();
		ActorDefinition.bufferOffsets = new int[ActorDefinition.size];
		int offset = 2;
		for (int bufferIndex = 0; bufferIndex < ActorDefinition.size; bufferIndex++) {
			ActorDefinition.bufferOffsets[bufferIndex] = offset;
			offset += buffer.getUnsignedLEShort();
		}

		ActorDefinition.cache = new ActorDefinition[20];
		for (int cacheIndex = 0; cacheIndex < 20; cacheIndex++)
			ActorDefinition.cache[cacheIndex] = new ActorDefinition();

	}

	public Model getChildModel(int frameId2, int frameId, int[] framesFrom2) {
		if (childrenIds != null) {
			ActorDefinition childDefinition = getChildDefinition();
			if (childDefinition == null)
				return null;
			else
				return childDefinition.getChildModel(frameId2, frameId, framesFrom2);
		}
		Model childIdModel = (Model) modelCache.get(id);
		if (childIdModel == null) {
			boolean cached = false;
			for (int modelId : modelIds)
				if (Model.isCached(modelId))
					cached = true;

			if (!cached)
				return null;
			Model[] childModels = new Model[modelIds.length];
			for (int model = 0; model < modelIds.length; model++)
				childModels[model] = Model.getModel(modelIds[model]);

			if (childModels.length == 1)
				childIdModel = childModels[0];
			else
				childIdModel = new Model(childModels.length,
						childModels);
			if (modifiedModelColors != null) {
				for (int color = 0; color < modifiedModelColors.length; color++)
					childIdModel.replaceColor(modifiedModelColors[color], originalModelColors[color]);

			}
			childIdModel.createBones();
			childIdModel.applyLighting(64 + brightness, 850 + contrast, -30, -50, -30, true);
			modelCache.put(childIdModel, id);
		}
		Model childModel = Model.aModel1614;
		childModel.replaceWithModel(childIdModel, Animation.exists(frameId2) & Animation.exists(frameId)
		);
		if (frameId2 != -1 && frameId != -1)
			childModel.mixAnimationFrames(frameId, 0, frameId2, framesFrom2);
		else if (frameId2 != -1)
			childModel.applyTransform(frameId2);
		if (sizeXZ != 128 || sizeY != 128)
			childModel.scaleT(sizeY, sizeXZ, 9, sizeXZ);
		childModel.calculateDiagonals();
		childModel.triangleSkin = null;
		childModel.vectorSkin = null;
		if (boundaryDimension == 1)
			childModel.oneSquareModel = true;
		return childModel;
	}

	public ActorDefinition getChildDefinition() {
		int childId = -1;
		if (varBitId != -1) {
			Varbit varbit = Varbit.cache[varBitId];
			int configId = varbit.configId;
			int leastSignificantBit = varbit.leastSignificantBit;
			int mostSignificantBit = varbit.mostSignificantBit;
			int bit = client.BITFIELD_MAX_VALUE[mostSignificantBit - leastSignificantBit];
			childId = client.widgetSettings[configId] >> leastSignificantBit & bit;
		} else if (settingId != -1)
			childId = client.widgetSettings[settingId];
		if (childId < 0 || childId >= childrenIds.length || childrenIds[childId] == -1)
			return null;
		else
			return getDefinition(childrenIds[childId]);
	}




}
