FROM ubuntu:22.04

# Avoid prompts from apt
ENV DEBIAN_FRONTEND=noninteractive

# Install Redis and sudo
RUN apt-get update && apt-get install -y \
    redis-server \
    sudo \
    && rm -rf /var/lib/apt/lists/*

# Create necessary directories with proper permissions
RUN mkdir -p /data /usr/local/etc/redis \
    && chown -R redis:redis /data /usr/local/etc/redis \
    && chmod -R 755 /data /usr/local/etc/redis

# Add redis user to sudoers with NOPASSWD for redis-server
RUN echo "redis ALL=(ALL) NOPASSWD: /usr/bin/redis-server" >> /etc/sudoers

# Set working directory
WORKDIR /data

# Switch to redis user
USER redis

# Expose the Sentinel port
EXPOSE 5001 5002 5003

# Default command (will be overridden by docker-compose)
CMD ["sudo", "redis-server", "/usr/local/etc/redis/redis-sentinel.conf", "--sentinel"]
