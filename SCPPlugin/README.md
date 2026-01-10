# SCP Plugin - Advanced Minecraft SCP Events

A comprehensive Minecraft plugin featuring SCP-themed events, custom mobs, and advanced server optimization.

## Features

### 🌙 Moon Events
- **Full Moon SCP Event**: 15% chance during full moon nights
  - Transforms all mobs into "Wish I Knew" SCP variants
  - Enhanced health, speed, and damage (2 hearts per hit)
  - Atmospheric effects and custom names
  - Black and white neon sky effects

- **Half Moon Fisher Event**: 25% chance during half moon nights
  - Spawns 2 Fisher mobs per loaded chunk
  - Special hook abilities to pull players
  - Custom drops and enhanced stats

### 🧟 SCP Mob Variants
- All hostile mobs transform during full moon events
- Deal 2 hearts (4 damage) per attack
- Enhanced health based on mob type + multiplier
- Speed boost and damage resistance
- Glowing effect for visibility
- Custom SCP-themed names

### 🎣 The Fisher
- Custom mob that spawns during half moon events
- Can hook players from 8 blocks away
- Enhanced Drowned with special abilities
- Drops fishing rods, fish, and rare sea items
- Water-based particle effects

### ⚡ Advanced Lag Optimizer
- Automatic entity count management
- Item despawn optimization
- Experience orb merging
- Memory usage monitoring
- Configurable limits and intervals
- Garbage collection when needed

### 🎛️ Professional Management Panel
- GUI-based plugin management (`/scppanel`)
- Real-time server statistics
- Event control and monitoring
- Optimizer configuration
- Professional design with intuitive navigation

## Commands

- `/scp fullmoon` - Force activate full moon event
- `/scp halfmoon` - Force activate half moon event
- `/scp disable fullmoon` - Disable full moon events
- `/scp disable halfmoon` - Disable half moon events
- `/scp disable` - Disable all SCP events
- `/scppanel` - Open management panel (GUI)

## Permissions

- `scp.admin` - Access to all SCP commands and panel (default: op)

## Installation

1. Download the plugin JAR file
2. Place it in your server's `plugins` folder
3. Restart your server
4. Configure settings in `plugins/SCPPlugin/config.yml`
5. Use `/scppanel` to access the management interface

## Configuration

The plugin creates a detailed configuration file with options for:
- Event chances and timing
- Mob health and damage values
- Fisher spawn settings
- Lag optimizer parameters
- Sky effect intensity

## Building from Source

1. Clone this repository
2. Ensure you have Maven installed
3. Run `mvn clean package`
4. Find the compiled JAR in the `target` folder

## Requirements

- Minecraft Server 1.19+
- Spigot or Paper server
- Java 8 or higher

## Support

For issues, suggestions, or contributions, please create an issue on the project repository.

## Version

Current Version: 1.0.0

---

**Note**: This plugin is designed for advanced server administrators who want comprehensive SCP-themed events with professional management tools and optimization features.