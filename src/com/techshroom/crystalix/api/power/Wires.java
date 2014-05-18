package com.techshroom.crystalix.api.power;

import java.util.*;

import net.minecraft.block.BlockSourceImpl;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import pathfinder.*;

import com.techshroom.crystalix.*;
import com.techshroom.crystalix.Util.*;
import com.techshroom.crystalix.block.CrystalixWire;
import com.techshroom.crystalix.pathsearch.GraphBlockSource;
import com.techshroom.crystalix.world.nbt.CrystalixCustomNBT;

/**
 * The Wires class handles the power flow of wiring.
 * 
 * @author Kenzie Togami
 */
public final class Wires {
    private static final HashMap<World, Graph> master = new HashMap<World, Graph>();
    private static HashMap<World, List<GraphBlockSource>> discarded = new HashMap<World, List<GraphBlockSource>>();

    /**
     * Traces from the given block to a provider and returns the location of the
     * provider or null if there is none.
     * 
     * @param i
     *            - start location
     * @return a {@link IBlockSource} for the {@link IPowerProvider} found, or
     *         null if none found.
     */
    public static IBlockSource wireTraceToProvider(IBlockSource i) {
        sync(i);
        Graph master = getGraph(i);
        master.compact();
        GraphNode start = getNodeAt(i);
        if (start == null) {
            return null;
        }
        GraphNode[] possibleEnds = lookForEnds(i);
        GraphSearch_Dijkstra d = new GraphSearch_Dijkstra(master);
        LinkedList<GraphNode> list = null;
        LinkedList<IBlockSource> conv = null;
        for (GraphNode end : possibleEnds) {
            start.id();
            list = d.search(start.id(), end.id(), true);
            if (list != null && !list.isEmpty()) {
                conv = convertToIBS(list, i);
                IBlockSource check = conv.getLast();
                if (IBS.equal(i, check)) {
                    // don't move power from start -> start
                    continue;
                }
                IPowerProvider ipp = IBS.provider(check);
                if (ipp != null && ipp.hasPower()) {
                    break;
                } else if (ipp != null) {

                }
            }
        }
        if (conv == null || conv.isEmpty()) {
            // no path found
            return null;
        }
        IBlockSource check = conv.getLast();
        if (IBS.equal(i, check)) {
            // don't move power from start -> start
            return null;
        }
        return check;
    }

    private static Graph getGraph(IBlockSource i) {
        Graph g = master.get(i.getWorld());
        if (g == null) {
            g = new Graph();
            master.put(i.getWorld(), g);
        }
        return g;
    }

    private static LinkedList<IBlockSource> convertToIBS(
            LinkedList<GraphNode> list, IBlockSource i) {
        LinkedList<IBlockSource> out = new LinkedList<IBlockSource>();
        for (GraphNode node : list) {
            IBlockSource match = null;
            if (node instanceof GraphBlockSource) {
                match = (GraphBlockSource) node;
            } else {
                match = new BlockSourceImpl(i.getWorld(), (int) node.x(),
                        (int) node.y(), (int) node.z());
            }
            out.add(match);
        }
        return out;
    }

    private static GraphNode[] lookForEnds(IBlockSource i) {
        Graph master = getGraph(i);
        GraphNode[] all = master.getNodeArray();
        List<IBlockSource> res = new ArrayList<IBlockSource>();
        for (GraphNode test : all) {
            if (test instanceof GraphBlockSource
                    && IBS.provider((GraphBlockSource) test) != null) {
                res.add((GraphBlockSource) test);
            }
        }
        IBS.setOverride(IBlockSource.class);
        res = IBS.sortByDistFrom(i, res);
        IBS.unsetOverride();
        GraphNode[] nodes = new GraphNode[res.size()];
        for (int j = 0; j < res.size(); j++) {
            nodes[j] = getNodeAt(res.get(j));
        }
        return nodes;
    }
    
    public static int getOutputCount(IBlockSource i) {
        return lookForEnds(i).length;
    }

    /**
     * Adds the give location to the node map for tracing, Must be called by
     * {@link IPowerProvider} and {@link IPowerReceiver} when placed in order to
     * trace the power right.
     * 
     * @param add
     *            - the {@link IBlockSource} of the location to add.
     */
    public static void addIBSToMap(IBlockSource add) {
        sync(add);
        Graph master = getGraph(add);
        if (hasNodeAt(add)) {
            return;
        }
        GraphBlockSource gbs = convOne(add);
        master.addNode(gbs);
        buildEdgesTo(gbs);
        save(add);
    }

    /**
     * Builds edges connecting the given {@link GraphBlockSource} to the
     * network.
     * 
     * @param gbs
     *            - the connection source
     */
    private static void buildEdgesTo(GraphBlockSource gbs) {
        Graph master = getGraph(gbs);
        // ensure it returns IBlockSource
        IBS.setOverride(IBlockSource.class);
        IBlockSource[] nearby = IBS.neighbors((IBlockSource) gbs);
        IBS.unsetOverride();
        for (IBlockSource n : nearby) {
            GraphBlockSource node = (GraphBlockSource) getNodeAt(n);
            if (node != null) {
                master.addEdge(node.id(), gbs.id(),
                        CrystalixWire.levelFromMeta(node.getBlockMetadata()),
                        CrystalixWire.levelFromMeta(gbs.getBlockMetadata()));
            }
        }
    }

    /**
     * Single arg version of {@link #conv(IBlockSource...)}.
     * 
     * @param one
     * @return the given {@link IBlockSource} to convert to a
     *         {@link GraphBlockSource}.
     */
    private static GraphBlockSource convOne(IBlockSource one) {
        return conv(one)[0];
    }

    /**
     * Converts the given {@link IBlockSource}s to {@link GraphBlockSource}s.
     * 
     * @param wrapped
     *            - the list of {@link IBlockSource} to convert.
     * @return - the converted {@link GraphBlockSource}s.
     */
    private static GraphBlockSource[] conv(IBlockSource... wrapped) {
        List<GraphBlockSource> discarded = getDisc(wrapped[0]);
        GraphBlockSource[] out = new GraphBlockSource[wrapped.length];
        int index = 0;
        for (IBlockSource add : wrapped) {
            GraphBlockSource gbs = null;
            // interestingly our version of contains means that IBS add could
            // have the same location as a GBS, so this works.
            int i = IBS.indexOf(discarded, add);
            if (i != -1) {
                if (add instanceof GraphBlockSource) {
                    gbs = (GraphBlockSource) add;
                }
                // id reuse
                GraphBlockSource tmp = discarded.get(i);
                if (gbs != null) {
                    // set the old id
                    gbs.freeID(tmp.id());
                }
                gbs = tmp;
            } else if (!(add instanceof GraphBlockSource)) {
                gbs = new GraphBlockSource(add.getWorld(), add.getXInt(),
                        add.getYInt(), add.getZInt());
            } else {
                gbs = (GraphBlockSource) add;
            }
            out[index] = gbs;
            index++;
        }
        return out;
    }

    private static List<GraphBlockSource> getDisc(IBlockSource i) {
        List<GraphBlockSource> g = discarded.get(i.getWorld());
        if (g == null) {
            g = new ArrayList<GraphBlockSource>();
            discarded.put(i.getWorld(), g);
        }
        return g;
    }

    /**
     * Removes the given location from the map. Must be called by
     * {@link IPowerProvider} and {@link IPowerReceiver} on removal or the map
     * will try to trace through them!
     * 
     * @param rem
     *            - the {@link IBlockSource} to remove.
     */
    public static void removeIBSFromMap(IBlockSource rem) {
        sync(rem);
        List<GraphBlockSource> discarded = getDisc(rem);
        Graph master = getGraph(rem);
        GraphNode remmatch = getNodeAt(rem);
        if (remmatch != null) {
            master.removeNode(remmatch.id());
            if (remmatch instanceof GraphBlockSource) {
                ((GraphBlockSource) remmatch).freeID(-1);
                discarded.add((GraphBlockSource) remmatch);
            }
        } else {
            CrystalixMain.modLog.warn("Didn't remove non-existant node "
                    + IBS.string(rem));
        }
        save(rem);
    }

    /**
     * Warning: doesn't save, just marks dirty so it saves next time possible.
     * Also syncs data.
     */
    private static void save(IBlockSource i) {
        sync(i).markDirty();
    }

    /**
     * Syncs data with disk data, sort of like loading data. Returns the
     * {@link CrystalixCustomNBT} for the given {@link IBlockSource}'s world.
     */
    private static CrystalixCustomNBT sync(IBlockSource i) {
        return CrystalixCustomNBT.forWorld(i.getWorld());
    }

    private static boolean hasNodeAt(IBlockSource loc) {
        return getNodeAt(loc) != null;
    }

    private static GraphNode getNodeAt(IBlockSource loc) {
        Graph master = getGraph(loc);
        // 0.1 accounts for floating point errors in the stored int
        return master.getNodeAt(loc.getXInt(), loc.getYInt(), loc.getZInt(),
                0.1);
    }

    /**
     * Call this to store the wire data to an NBT tag. World needed.
     */
    public static void store(World w, NBTTagCompound nbt) {
        List<GraphBlockSource> discarded = Wires.discarded.get(w);
        Graph master = Wires.master.get(w);
        if (master == null || discarded == null) {
            // nothing to store
            return;
        }
        GraphNode[] nodes = master.getNodeArray();
        int[] nids = new int[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            GraphNode graphNode = nodes[i];
            nids[i] = graphNode.id();
        }
        nbt.setIntArray("idarr", nids);
        int[] nlocs = new int[nodes.length * 3];
        for (int i = 0, j = 0; i < nodes.length; i++, j += 3) {
            GraphNode graphNode = nodes[i];
            nlocs[j] = (int) graphNode.x();
            nlocs[j + 1] = (int) graphNode.y();
            nlocs[j + 2] = (int) graphNode.z();
        }
        nbt.setIntArray("locarr", nlocs);

        // discarded
        GraphNode[] dnodes = discarded.toArray(new GraphNode[0]);
        int[] dnids = new int[dnodes.length];
        for (int i = 0; i < dnodes.length; i++) {
            GraphNode graphNode = dnodes[i];
            dnids[i] = graphNode.id();
        }
        nbt.setIntArray("d-idarr", dnids);
        int[] dnlocs = new int[dnodes.length * 3];
        for (int i = 0, j = 0; i < dnodes.length; i++, j += 3) {
            GraphNode graphNode = dnodes[i];
            dnlocs[j] = (int) graphNode.x();
            dnlocs[j + 1] = (int) graphNode.y();
            dnlocs[j + 2] = (int) graphNode.z();
        }
        nbt.setIntArray("d-locarr", dnlocs);
    }

    /**
     * Call this to load the wire data from an NBT tag.
     */
    public static void load(World world, NBTTagCompound nbt) {
        int NBTIAID = NBT.TAG_INT_ARRAY;

        // load data
        int[] ids = new int[0], locs = new int[0], dids = new int[0], dlocs = new int[0];
        if (nbt.hasKey("idarr", NBTIAID)) {
            ids = nbt.getIntArray("idarr");
        }
        if (nbt.hasKey("locarr", NBTIAID)) {
            locs = nbt.getIntArray("locarr");
        }
        if (nbt.hasKey("d-idarr", NBTIAID)) {
            dids = nbt.getIntArray("d-idarr");
        }
        if (nbt.hasKey("d-locarr", NBTIAID)) {
            dlocs = nbt.getIntArray("d-locarr");
        }
        if (ids.length * 3 != locs.length) {
            throw new IllegalStateException("elements off: " + ids.length + ":"
                    + locs.length);
        }
        if (dids.length * 3 != dlocs.length) {
            throw new IllegalStateException("elements off: " + dids.length
                    + ":" + dlocs.length);
        }
        GraphBlockSource[] drebuilt = new GraphBlockSource[dids.length];

        // load saved graph
        Graph g = new Graph(ids.length);
        // store graph now so that buildEdgesTo works
        master.put(world, g);
        for (int i = 0, j = 0; i < ids.length; i++, j += 3) {
            GraphBlockSource src = new GraphBlockSource(world, locs[j],
                    locs[j + 1], locs[j + 2], ids[i]);
            g.addNode(src);
            buildEdgesTo(src);
        }

        // load discarded
        for (int i = 0, j = 0; i < dids.length; i++, j += 3) {
            drebuilt[i] = new GraphBlockSource(world, dlocs[j], dlocs[j + 1],
                    dlocs[j + 2], dids[i]);
        }
        discarded.put(world,
                new ArrayList<GraphBlockSource>(Arrays.asList(drebuilt)));
    }
}
