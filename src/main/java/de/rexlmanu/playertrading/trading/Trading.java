/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.playertrading.trading;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class Trading {

    public static Trading createTrading(Player inviter, Player acceptor) {
        return new Trading(inviter, acceptor);
    }

    private Player inviter, acceptor;
    private boolean accepted;
    private long invitedSendAt;

    private Trading(Player inviter, Player acceptor) {
        this.inviter = inviter;
        this.acceptor = acceptor;
        this.accepted = false;
        this.invitedSendAt = System.currentTimeMillis();
    }
}
