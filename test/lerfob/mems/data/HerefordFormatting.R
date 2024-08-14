#'
#' Formatting Hereford data
#' @author Mathieu Fortin - August 2024
#'

data <- read.csv("Hereford.csv")
data <- data[which(data$SoilDepthCm %in% c("0-15", "FH")),]

write.csv(data, file = "HerefordFormatted.csv", row.names = F)


message("Mean pH is ", mean(data$pH, na.rm = T))
message("Mean bulk density is ", mean(data[which(data$SoilDepthCm == "0-15"), "BDg_cm3"], na.rm = T))
message("Mean sand proportion is ", mean(data[which(data$SoilDepthCm == "0-15"), "SandPerc"], na.rm = T))
message("Mean rock proportion is ", mean(data[which(data$SoilDepthCm == "0-15"), "RockPerc"], na.rm = T))
