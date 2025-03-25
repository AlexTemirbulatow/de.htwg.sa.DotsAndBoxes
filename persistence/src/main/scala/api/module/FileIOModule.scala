package api.module

import fileIoComponent.FileIOInterface
import fileIoComponent.jsonImpl.FileIO

object FileIOModule:
  given FileIOInterface = new FileIO
