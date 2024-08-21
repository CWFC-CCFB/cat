#'
#' Formatting Hereford data
#' @author Mathieu Fortin - August 2024
#'

data <- read.csv("Hereford.csv")
data <- data[which(data$SoilDepthCm %in% c("0-15", "FH")),]

write.csv(data, file = "HerefordFormatted.csv", row.names = F)


mean(data[which(data$SoilDepthCm == "FH"), "SOCMgC_ha"])
var(data[which(data$SoilDepthCm == "FH"), "SOCMgC_ha"])
hist(data[which(data$SoilDepthCm == "FH"), "SOCMgC_ha"])

mean(data[which(data$SoilDepthCm == "0-15"), "SOCMgC_ha"])
var(data[which(data$SoilDepthCm == "0-15"), "SOCMgC_ha"])
hist(data[which(data$SoilDepthCm == "0-15"), "SOCMgC_ha"])

message("Mean pH is ", mean(data$pH, na.rm = T))
message("Mean bulk density is ", mean(data[which(data$SoilDepthCm == "0-15"), "BDg_cm3"], na.rm = T))
message("Mean sand proportion is ", mean(data[which(data$SoilDepthCm == "0-15"), "SandPerc"], na.rm = T))
message("Mean rock proportion is ", mean(data[which(data$SoilDepthCm == "0-15"), "RockPerc"], na.rm = T))

##### Final MCMC sample #####

mcmcSample <- read.csv("parameterEstimatesSet_Hereford.csv", sep=";")
mcmcSample$i <- 1:nrow(mcmcSample)

require(ggplot2)
ggplot() + geom_point(aes(x=i, y=LLK), mcmcSample)
hist(mcmcSample$parmB1)
hist(mcmcSample$parmB2)
hist(mcmcSample$parmB3)
hist(mcmcSample$LIT_frg)
hist(mcmcSample$POM_split)
hist(mcmcSample$DOC_frg)
hist(mcmcSample$DOC_lch)
hist(mcmcSample$parmK1)



hist(mcmcSample$sigma2Litter)
hist(mcmcSample$sigma2Soil)
hist(mcmcSample$parmB1)
hist(mcmcSample$parmB1)

