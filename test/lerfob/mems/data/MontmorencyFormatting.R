#'
#' Formatting Montmorency data
#' @author Mathieu Fortin - August 2024
#'

data <- read.csv("ChronosequenceFOM.csv")
data <- data[which(data$SoilDepthCm %in% c("0-15", "FH")),]

write.csv(data, file = "ChronosequenceFOMFormatted.csv", row.names = F)


mean(data[which(data$SoilDepthCm == "FH"), "SocMgC_ha"])
var(data[which(data$SoilDepthCm == "FH"), "SocMgC_ha"])
mean(data[which(data$SoilDepthCm == "0-15"), "SocMgC_ha"])
var(data[which(data$SoilDepthCm == "0-15"), "SocMgC_ha"])


##### Final MCMC sample #####

mcmcSample <- read.csv("parameterEstimatesSet_Montmorency.csv", sep=";")
mcmcSample$i <- 1:nrow(mcmcSample)

require(ggplot2)
ggplot() + geom_point(aes(x=i, y=LLK), mcmcSample)
