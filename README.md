# Food Waste Reduction Platform
## Overview
A Java-based desktop application designed to reduce food waste by connecting food donors with charitable organizations. The platform facilitates surplus food redistribution through an intuitive graphical interface.

## Features
User authentication (Donors/Charities/Admins)

Food inventory management

Donation scheduling and tracking

Real-time availability updates

Search and filter donations

Analytics dashboard

## Technologies
Java (Core Logic)

Java Swing (GUI)

JDBC (Database Connectivity)

## Usage
Register as Donor or Charity

Donors: Add food items with expiry details

Charities: Browse and claim available donations

Schedule pickups/deliveries

## Database Schema
Users (id, name, type, contact, address)

FoodItems (id, donor_id, name, quantity, expiry)

Donations (id, food_id, charity_id, status, date)

Transactions (id, donation_id, pickup_time, status)

## Installation
Clone repository

Configure database connection in config.properties

Compile and run PlatfromGUI.java
