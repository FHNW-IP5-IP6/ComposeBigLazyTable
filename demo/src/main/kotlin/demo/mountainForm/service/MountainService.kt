package demo.mountainForm.service

interface MountainService {
    operator fun get(id: Long): MountainDTO

    fun save(dto: MountainDTO)

    fun getTotalCount(): Long
}