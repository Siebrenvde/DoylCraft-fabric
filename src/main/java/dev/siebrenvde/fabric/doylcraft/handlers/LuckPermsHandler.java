package dev.siebrenvde.fabric.doylcraft.handlers;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LuckPermsHandler {

    private static final LuckPerms luckPerms = LuckPermsProvider.get();

    public static Set<Group> getGroups() {
        return luckPerms.getGroupManager().getLoadedGroups();
    }

    public static CompletableFuture<String> getPlayerGroup(ServerPlayerEntity player) {
        return luckPerms.getUserManager().loadUser(player.getUuid())
            .thenApplyAsync(user -> {
                Set<String> groups = user.getNodes(NodeType.INHERITANCE).stream()
                    .map(InheritanceNode::getGroupName)
                    .collect(Collectors.toSet());
                return groups.iterator().next();
            });
    }

    public static void setPlayerGroup(ServerPlayerEntity player, String groupName) {
        Group group = luckPerms.getGroupManager().getGroup(groupName);
        luckPerms.getUserManager().modifyUser(player.getUuid(), (User user) -> {
            user.data().clear(NodeType.INHERITANCE::matches);
            Node node = InheritanceNode.builder(group).build();
            user.data().add(node);
        });
    }

    public static boolean groupExists(String group) {
        for(Group g : getGroups()) {
            if(g.getName().toLowerCase().equalsIgnoreCase(group)) {
                return true;
            }
        }
        return false;
    }

}
