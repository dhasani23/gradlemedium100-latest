package com.gradlemedium100.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import com.gradlemedium100.security.EncryptionService;

/**
 * Configuration class that sets up the payment gateway connections and API keys in a secure manner.
 * This class is responsible for providing properly configured payment gateway client with
 * appropriate credentials and connection parameters.
 */
@Configuration
@PropertySource("classpath:payment-gateway.properties") // Properties file for payment gateway config
public class PaymentGatewayConfig {

    @Value("${payment.gateway.api.key}")
    private String apiKey;

    @Value("${payment.gateway.api.secret}")
    private String apiSecret;

    @Value("${payment.gateway.url}")
    private String gatewayUrl;
    
    @Value("${payment.gateway.provider:default}")
    private String gatewayProvider;

    // Autowire the encryption service for secure handling of sensitive data
    private final EncryptionService encryptionService;
    
    // Inject environment for conditional configuration
    private final Environment environment;

    /**
     * Constructor for dependency injection
     * 
     * @param encryptionService Service for encrypting and decrypting sensitive payment data
     * @param environment Spring environment for profile-specific configurations
     */
    public PaymentGatewayConfig(EncryptionService encryptionService, Environment environment) {
        this.encryptionService = encryptionService;
        this.environment = environment;
    }

    /**
     * Creates and configures the payment gateway client with the necessary credentials.
     * The client is properly configured with API credentials and connection parameters.
     *
     * @return A configured PaymentGatewayClient ready for processing payments
     */
    @Bean
    public PaymentGatewayClient gatewayClient() {
        // Create a new client instance
        PaymentGatewayClient client = new PaymentGatewayClient();
        
        // Configure with decrypted credentials
        client.setApiKey(getDecryptedApiKey());
        client.setApiSecret(getDecryptedApiSecret());
        client.setGatewayUrl(gatewayUrl);
        
        // Configure connection parameters
        client.setConnectionTimeout(30000); // 30 seconds timeout
        client.setReadTimeout(60000);      // 60 seconds read timeout
        
        // Configure environment-specific settings
        if (isProductionEnvironment()) {
            client.enableProductionMode();
            // TODO: Add additional production-specific configurations
        } else {
            client.enableSandboxMode();
            // FIXME: Sandbox mode needs proper error simulation configuration
        }
        
        return client;
    }

    /**
     * Retrieves and decrypts the API key for the payment gateway.
     * This method ensures that the API key is never stored in plain text.
     *
     * @return Decrypted API key as a String
     */
    public String getDecryptedApiKey() {
        try {
            return encryptionService.decrypt(apiKey);
        } catch (Exception e) {
            // Log the error but don't expose the actual exception details which might contain sensitive info
            throw new RuntimeException("Failed to decrypt payment gateway API key", e);
        }
    }

    /**
     * Retrieves and decrypts the API secret for the payment gateway.
     * This method ensures that the API secret is never stored in plain text.
     *
     * @return Decrypted API secret as a String
     */
    public String getDecryptedApiSecret() {
        try {
            return encryptionService.decrypt(apiSecret);
        } catch (Exception e) {
            // Log the error but don't expose the actual exception details
            throw new RuntimeException("Failed to decrypt payment gateway API secret", e);
        }
    }
    
    /**
     * Gets the default gateway provider configured for the application.
     * 
     * @return The name of the default payment gateway provider
     */
    public String getDefaultGatewayProvider() {
        return gatewayProvider;
    }
    
    /**
     * Helper method to determine if we're running in production environment
     * 
     * @return true if running in production, false otherwise
     */
    private boolean isProductionEnvironment() {
        for (String profile : environment.getActiveProfiles()) {
            if ("production".equals(profile)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Placeholder class for PaymentGatewayClient.
     * This would typically be a class provided by the payment gateway vendor or a custom implementation
     * that handles the HTTP communication with the payment gateway.
     */
    public static class PaymentGatewayClient {
        private String apiKey;
        private String apiSecret;
        private String gatewayUrl;
        private int connectionTimeout;
        private int readTimeout;
        private boolean productionMode = false;
        
        // Getters and setters
        
        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
        
        public void setApiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
        }
        
        public void setGatewayUrl(String gatewayUrl) {
            this.gatewayUrl = gatewayUrl;
        }
        
        public void setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }
        
        public void setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
        }
        
        public void enableProductionMode() {
            this.productionMode = true;
        }
        
        public void enableSandboxMode() {
            this.productionMode = false;
        }
        
        // TODO: Implement actual payment processing methods
    }
}