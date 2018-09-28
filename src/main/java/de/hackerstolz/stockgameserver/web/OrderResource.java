package de.hackerstolz.stockgameserver.web;

import de.hackerstolz.stockgameserver.config.SecurityUtils;
import de.hackerstolz.stockgameserver.exception.InsufficientFundsException;
import de.hackerstolz.stockgameserver.exception.InsufficientSharesException;
import de.hackerstolz.stockgameserver.exception.StockNotFoundException;
import de.hackerstolz.stockgameserver.model.Order;
import de.hackerstolz.stockgameserver.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order/")
public class OrderResource {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final OrderService orderService;

    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody final Order order) {
        if (order.getIsBuy() == null || order.getAmount() == null || order.getSymbol() == null) {
            log.info("Invalid order: {}", order);
            return ResponseEntity.badRequest().build();
        }

        try {
            final String userId = SecurityUtils.getCurrentUserLoginAsString();
            return ResponseEntity.ok(orderService.placeOrder(order, userId));
        } catch (final StockNotFoundException e) {
            return ResponseEntity.status(404).body("The symbol " + e.getSymbol() + " could not be found. If it's not a "
                    + "typo, then please report this to the admin.");
        } catch (final InsufficientFundsException e) {
            return ResponseEntity.status(400).body(
                    "Insufficient Funds. You don't have the required " + e.getRequiredAmount().intValue());
        } catch (final InsufficientSharesException e) {
            return ResponseEntity.status(400).body(
                    "Insufficient Shares. For the given symbol you only have " + e.getAvailableShares() + " shares");
        }
    }
}
