package me.choco.LSaddon.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import org.bukkit.Material;

import me.choco.locksecurity.api.LockedBlock;

public class CollectorBlock {
	
	private final int id;
	private final LockedBlock block;
	private final Set<Material> materials;
	
	public CollectorBlock(LockedBlock block, int id, Material... materials) {
		this.block = block;
		this.id = id;
		this.materials = new HashSet<>(Arrays.asList(materials));
	}
	
	public CollectorBlock(LockedBlock block, int id) {
		this.block = block;
		this.id = id;
		this.materials = new HashSet<>();
	}
	
	public LockedBlock getBlock() {
		return block;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean shouldCollect(Material material) {
		return materials.contains(material);
	}
	
	public void addCollectionMaterial(Material material) {
		this.materials.add(material);
	}
	
	public void removeCollectionMaterial(Material material) {
		this.materials.remove(material);
	}
	
	public Set<Material> getCollectionMaterials() {
		return ImmutableSet.copyOf(materials);
	}
	
	public void clearCollectionMaterials() {
		this.materials.clear();
	}
	
}