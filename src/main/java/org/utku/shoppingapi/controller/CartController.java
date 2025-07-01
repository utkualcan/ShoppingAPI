package org.utku.shoppingapi.controller;

    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.utku.shoppingapi.entity.Cart;
    import org.utku.shoppingapi.service.CartService;

    import java.util.List;

    @RestController
    @RequestMapping("/api/cart")
    public class CartController {

        private final CartService cartService;

        public CartController(CartService cartService) {
            this.cartService = cartService;
        }

        @GetMapping
        public List<Cart> getAllCarts() {
            return cartService.getAllCarts();
        }

        @GetMapping("/user/{userId}")
        public List<Cart> getCartByUserId(@PathVariable Long userId) {
            return cartService.getCartByUserId(userId);
        }

        @PostMapping
        public ResponseEntity<Cart> createCart(@RequestBody Cart cart) {
            return ResponseEntity.ok(cartService.createCart(cart));
        }

        @PutMapping("/{id}")
        public ResponseEntity<Cart> updateCart(@PathVariable Long id, @RequestBody Cart cart) {
            return ResponseEntity.ok(cartService.updateCart(id, cart));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
            cartService.deleteCart(id);
            return ResponseEntity.noContent().build();
        }
        @GetMapping("/{id}")
        public ResponseEntity<Cart> getCartById(@PathVariable Long id) {
            return cartService.getAllCarts().stream()
                    .filter(cart -> cart.getId().equals(id))
                    .findFirst()
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        @PostMapping("/{cartId}/items")
        public ResponseEntity<Cart> addItemToCart(@PathVariable Long cartId, @RequestParam Long productId, @RequestParam int quantity) {
            return ResponseEntity.ok(cartService.addItemToCart(cartId, productId, quantity));
        }

        @PutMapping("/{cartId}/items/{productId}")
        public ResponseEntity<Cart> updateItemQuantity(@PathVariable Long cartId, @PathVariable Long productId, @RequestParam int quantity) {
            return ResponseEntity.ok(cartService.updateItemQuantity(cartId, productId, quantity));
        }

        @DeleteMapping("/{cartId}/items/{productId}")
        public ResponseEntity<Cart> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
            return ResponseEntity.ok(cartService.removeItemFromCart(cartId, productId));
        }
    }