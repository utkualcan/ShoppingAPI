package org.utku.shoppingapi.service;

    import org.springframework.stereotype.Service;
    import org.utku.shoppingapi.entity.Cart;
    import org.utku.shoppingapi.entity.CartItem;
    import org.utku.shoppingapi.entity.Product;
    import org.utku.shoppingapi.repository.CartRepository;
    import org.utku.shoppingapi.repository.ProductRepository;

    import java.util.List;
    import java.util.Optional;

    @Service
    public class CartServiceImpl implements CartService {

        private final CartRepository cartRepository;
        private final ProductRepository productRepository;

        public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository) {
            this.cartRepository = cartRepository;
            this.productRepository = productRepository;
        }

        @Override
        public List<Cart> getAllCarts() {
            return cartRepository.findAll();
        }

        @Override
        public List<Cart> getCartByUserId(Long userId) {
            return cartRepository.findByUserId(userId);
        }

        @Override
        public Cart createCart(Cart cart) {
            return cartRepository.save(cart);
        }

        @Override
        public Cart updateCart(Long id, Cart cart) {
            Optional<Cart> existing = cartRepository.findById(id);
            if (existing.isPresent()) {
                cart.setId(id);
                return cartRepository.save(cart);
            }
            throw new RuntimeException("Cart not found");
        }

        @Override
        public void deleteCart(Long id) {
            cartRepository.deleteById(id);
        }

        @Override
        public Cart addItemToCart(Long cartId, Long productId, int quantity) {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            CartItem item = cart.findItemByProduct(product);
            if (item != null) {
                item.increaseQuantity(quantity);
            } else {
                item = new CartItem();
                item.setProduct(product);
                item.setQuantity(quantity);
                item.setUnitPrice(product.getPrice());
                cart.addItem(item);
            }
            return cartRepository.save(cart);
        }

        @Override
        public Cart updateItemQuantity(Long cartId, Long productId, int quantity) {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            CartItem item = cart.findItemByProduct(product);
            if (item == null) throw new RuntimeException("Item not found in cart");
            item.setQuantity(quantity);
            return cartRepository.save(cart);
        }

        @Override
        public Cart removeItemFromCart(Long cartId, Long productId) {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            CartItem item = cart.findItemByProduct(product);
            if (item != null) {
                cart.removeItem(item);
                return cartRepository.save(cart);
            }
            throw new RuntimeException("Item not found in cart");
        }
    }